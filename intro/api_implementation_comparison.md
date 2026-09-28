# 🛠️ Triển Khai Thực Tế: REST vs gRPC vs GraphQL vs SOAP

Lý thuyết thì biết rồi — nhưng khi **code thật**, mỗi API style yêu cầu một bộ công cụ, cấu trúc project và tư duy hoàn toàn khác nhau.

---

## 🏗️ 1. Cấu Trúc Project & Toolchain

| | REST | gRPC | GraphQL | SOAP |
|---|---|---|---|---|
| **Định nghĩa contract** | Không bắt buộc (OpenAPI/Swagger tùy chọn) | **Bắt buộc** `.proto` file | **Bắt buộc** Schema `.graphql` | **Bắt buộc** WSDL file (XML) |
| **Code generation** | Không (viết tay) | `protoc` tự sinh code | Codegen từ schema | `wsdl2java` tự sinh code |
| **Serialization** | JSON (text) | Protobuf (binary) | JSON (text) | XML (text) |
| **Thư viện cần thiết** | Gần như không | `grpc`, `protobuf` | `graphql`, `apollo-server` | `spring-ws`, `axis2` |

---

## 🔴 2. REST API — Viết Tay Tự Do

REST không có contract bắt buộc. Bạn **tự định nghĩa** endpoint, request body, response.

### Spring Boot (Java)
```java
// Controller — tự định nghĩa route
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        UserDTO user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequest req) {
        UserDTO created = userService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
```

```java
// DTO — bạn tự định nghĩa shape của JSON
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    // getters, setters...
}
```

### Đặc điểm khi triển khai REST:
- ✅ **Không cần bước generate code** — viết tay trực tiếp
- ✅ **Test dễ** — dùng Postman, curl ngay lập tức
- ✅ **Tài liệu** — dùng Swagger/OpenAPI để tự sinh docs
- ❌ **Không có type-safety giữa client & server** — nếu server đổi `name` → `fullName`, client lỗi runtime mới biết

---

## 🟢 3. gRPC — Contract-First với `.proto`

gRPC **bắt buộc** bạn phải định nghĩa contract trong file `.proto` **trước**, rồi mới code.

### Bước 1: Viết file `.proto` (ngôn ngữ IDL)
```protobuf
// user.proto — đây là "hợp đồng" giữa client & server
syntax = "proto3";

package user;

// Định nghĩa Service (giống interface)
service UserService {
  rpc GetUser (GetUserRequest) returns (UserResponse);
  rpc CreateUser (CreateUserRequest) returns (UserResponse);
  rpc ListUsers (ListUsersRequest) returns (stream UserResponse); // Streaming!
}

// Định nghĩa các Message (giống DTO)
message GetUserRequest {
  int64 id = 1;
}

message CreateUserRequest {
  string name = 2;
  string email = 3;
}

message UserResponse {
  int64 id = 1;
  string name = 2;
  string email = 3;
}
```

### Bước 2: Generate code từ `.proto`
```bash
# Chạy lệnh này → tự sinh ra Java/Go/Python classes
protoc --java_out=./src --grpc-java_out=./src user.proto
```

### Bước 3: Implement service trong Java
```java
// Implement class được generate tự động
@GrpcService
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    @Override
    public void getUser(GetUserRequest request, StreamObserver<UserResponse> responseObserver) {
        // request.getId() — type-safe, đúng kiểu int64
        User user = userRepository.findById(request.getId());

        UserResponse response = UserResponse.newBuilder()
            .setId(user.getId())
            .setName(user.getName())
            .setEmail(user.getEmail())
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
```

### Đặc điểm khi triển khai gRPC:
- ✅ **Type-safe tuyệt đối** — server đổi `.proto` → client compile lỗi ngay, không đến runtime
- ✅ **Hỗ trợ Streaming** (server-stream, client-stream, bidirectional)
- ✅ **Hiệu năng cực cao** — Protobuf binary nhỏ hơn JSON ~70%
- ❌ **Không test được bằng Postman thông thường** — phải dùng `grpcurl`, `BloomRPC`, hoặc `Postman v10+`
- ❌ **Không đọc được payload** trên wire (binary)
- ❌ **Không dùng được trên browser** trực tiếp (phải qua gRPC-Web gateway)

---

## 🔵 4. GraphQL — Schema Là Trung Tâm

GraphQL cũng **contract-first**, nhưng schema được viết bằng SDL (Schema Definition Language) thân thiện hơn `.proto`.

### Bước 1: Định nghĩa Schema
```graphql
# schema.graphql
type User {
  id: ID!
  name: String!
  email: String!
  posts: [Post!]!
}

type Post {
  id: ID!
  title: String!
  author: User!
}

type Query {
  user(id: ID!): User
  users: [User!]!
}

type Mutation {
  createUser(name: String!, email: String!): User!
}
```

### Bước 2: Implement Resolvers (Spring for GraphQL)
```java
@Controller
public class UserResolver {

    // Resolver cho Query.user
    @QueryMapping
    public User user(@Argument Long id) {
        return userService.findById(id);
    }

    // Resolver cho User.posts — chỉ gọi khi client hỏi field "posts"
    @SchemaMapping(typeName = "User", field = "posts")
    public List<Post> posts(User user) {
        return postService.findByAuthorId(user.getId());
    }

    @MutationMapping
    public User createUser(@Argument String name, @Argument String email) {
        return userService.create(name, email);
    }
}
```

### Client tự chọn dữ liệu:
```graphql
# Client A chỉ cần tên:
query { user(id: "1") { name } }

# Client B cần cả posts:
query {
  user(id: "1") {
    name
    email
    posts { title }
  }
}
```

### Đặc điểm khi triển khai GraphQL:
- ✅ **Một endpoint duy nhất** `/graphql` — không quản lý hàng trăm route
- ✅ **Client tự quyết định shape của response** — không over-fetch, không under-fetch
- ✅ **Tự sinh docs** qua GraphQL Playground / Introspection
- ❌ **N+1 problem nghiêm trọng hơn** — phải dùng DataLoader để batch queries
- ❌ **Caching phức tạp** — không cache đơn giản bằng HTTP Cache như REST

---

## ⚫ 5. SOAP — XML Từ Đầu Đến Cuối

SOAP dùng **WSDL** — một file XML mô tả toàn bộ service (thường dài hàng trăm dòng).

### Spring-WS Implementation
```java
@Endpoint
public class UserEndpoint {

    @PayloadRoot(namespace = "http://example.com/user", localPart = "GetUserRequest")
    @ResponsePayload
    public GetUserResponse getUser(@RequestPayload GetUserRequest request) {
        GetUserResponse response = new GetUserResponse();
        response.setId(request.getId());
        response.setName("Nguyen Van A");
        return response;
    }
}
```

### Request thực tế qua mạng:
```xml
POST /ws HTTP/1.1
Content-Type: text/xml

<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <GetUserRequest>
      <id>1</id>
    </GetUserRequest>
  </soap:Body>
</soap:Envelope>
```

---

## ⚡ 6. Bảng So Sánh Triển Khai Thực Tế

| Tiêu chí | REST | gRPC | GraphQL | SOAP |
|---|---|---|---|---|
| **Bước đầu tiên** | Viết Controller | Viết `.proto` | Viết Schema | Viết/lấy WSDL |
| **Code generation** | ❌ Không | ✅ `protoc` bắt buộc | ⚡ Tùy chọn | ✅ `wsdl2java` |
| **Type safety** | ❌ Yếu (runtime) | ✅ Mạnh (compile-time) | ✅ Mạnh (compile-time) | ✅ Mạnh (compile-time) |
| **Test tool** | Postman, curl | grpcurl, BloomRPC | GraphQL Playground | SoapUI |
| **Versioning** | `/api/v1/`, `/api/v2/` | `package v1`, `package v2` | `@deprecated` directive | Namespace mới |
| **Docs** | Swagger / OpenAPI | Reflection API | Introspection | WSDL tự mô tả |
| **Streaming** | SSE / WebSocket riêng | ✅ Native 4 loại stream | Subscriptions | ❌ Không |
| **Browser support** | ✅ Native | ❌ Cần gRPC-Web | ✅ Native | ✅ (nhưng nặng) |
| **Độ khó setup** | ⭐ Dễ nhất | ⭐⭐⭐ Khó | ⭐⭐ Trung bình | ⭐⭐⭐⭐ Khó nhất |

---

## 🎯 7. Decision Tree — Chọn Gì Trong Thực Tế?

```
Bạn đang build cái gì?
│
├─ Public API cho mobile/web app?
│   └─ → REST (dễ dùng, Postman-friendly, cache HTTP)
│
├─ Giao tiếp nội bộ giữa microservices (tốc độ quan trọng)?
│   └─ → gRPC (binary, streaming, type-safe)
│
├─ BFF (Backend For Frontend) — nhiều client khác nhau cần data shapes khác nhau?
│   └─ → GraphQL (flexible, tránh over-fetch)
│
└─ Tích hợp với hệ thống ngân hàng / tài chính / doanh nghiệp cũ?
    └─ → SOAP (bắt buộc, có WSDL sẵn)
```

> **💡 Thực tế tại các công ty lớn (Netflix, Shopee, Grab):** Dùng **REST** cho public API, **gRPC** cho internal microservices, và **GraphQL** cho mobile BFF — cả 3 cùng tồn tại trong một hệ thống!
