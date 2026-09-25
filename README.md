

#### 项目启动

所以以后本地开发一个很自然的顺序会是：

```
cd D:\personal-hub

docker compose up -d
```

先把依赖启动。

然后再启动：

```
Spring Boot
```

最后：

```
cd frontend
npm run dev
```

形成：

```
① Docker / PostgreSQL
       ↓
② Spring Boot
       ↓
③ Vue / Vite
```



#### 百炼密钥（放在环境变量里面）

```
sk-ws-H.PIMLRYE.uMYV.MEUCIAFYR9hdVeB2SvKTUqoNgpIY-_M037MVPhdbWofa-E31AiEAyhwl8LKuBJCaZOiKBPDjGMy4PvvUJfo2us9CL5eDRLg
```





#### 注解

##### @Entity

```
@Entity
```

这个 Java 类要映射到数据库表。

##### @Table

```
@Table(name = "conversations")
```

告诉 Hibernate：

```
Java：
Conversation

↓

PostgreSQL：
conversations
```

##### @Column 

就是在描述数据库列

##### @ManyToOne

它是在告诉 Hibernate：

> **很多 Message 可以属于同一个 Conversation。**



##### @JoinColumn

```
@JoinColumn(name = "conversation_id")
```

告诉 Hibernate：

> 在 `messages` 表里创建一个 `conversation_id` 列，用它关联 `conversations` 表。



#### iterm



##### JPA

= 一套 Java 持久化规范
= 规定 @Entity、@Id、@ManyToOne 这些东西是什么意思

##### Hibernate

= JPA 的具体实现
= 真正读取注解、生成 SQL、执行 ORM 工作

##### ORM

Object Relational Mapping
 对象关系映射。

ORM就在两边做翻译：

```
Java                          PostgreSQL

Conversation.class      ↔     conversations

id                       ↔     id

title                    ↔     title

ChatMessageEntity        ↔     messages

conversation             ↔     conversation_id

@ManyToOne               ↔     FOREIGN KEY
```

这就是 Hibernate 的核心价值。

所以 JPA/Hibernate 不是“替代数据库”。

而是：

> **让我们主要用 Java 对象操作数据库，然后由 ORM 帮我们转换成 SQL。**
