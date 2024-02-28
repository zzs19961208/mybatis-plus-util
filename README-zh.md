
# mybatis-plus-util

## 1. 什么是MyBatis-Plus?
MyBatis-Plus-Util是MyBatis-Plus的增强工具类,主要通过注解标注实体类快速构筑单表QueryWrapper的简单查询,提高开发效率。

## 2. 开始
- 增加 MyBatis-Plus-Util 依赖
  - Maven:
  - SpringBoot2

      ```xml    
	<dependency>
		<groupId>io.github.heathchen</groupId>
		<artifactId>mybatis-plus-util</artifactId>
		<version>Latest Version</version>    
	</dependency>    
```  
- Modify Entity file marks @CustomerQuery annotation  
  
	```java  
	@TableName(value ="pdt_spec")  
	public class PdtSpec  {    
		/**  
		 * id 
		 * 
		 * */    
		@TableId(value = "spec_id")   
		private Long specId;    
		/**  
		 * 规格名  
		 */
		@CustomerQuery(value = QueryType.LIKE)   
		@TableField(value = "spec_name")   
		private Long specName;    
		/**  
		 * 创建时间 
		 */
		@CustomerQuery(orderType = OrderType.ASC)   
		@TableField(value = "create_time")   
		private Date createTime;  
	
	}    
    
	```    
- 使用  
```java    
@PostMapping("pdtSpec-list")  
public TableDataInfo list(@RequestBody PdtSpec search) {    
	return getDataTable(queryByReflect(search));  }  
```    

- 示例 1:    
  RequestBody
    ```json    
    {    
    }    
    ```   
  MyBatis-Plus will execute the following SQL
    ```sql    
    SELECT * FROM pdt_spec ORDER BY create_time ASC    
```          - 示例 2:    
  RequestBody  
    ```json    
    {         
       "specName": "小"    
    }    
    ```     
	MyBatis-Plus will execute the following SQL  
	``` sql  
  SELECT * FROM pdt_spec  WHERE (spec_name LIKE '%小%')  ORDER BY create_time ASC    
	```    
- 示例 3:    
	  RequestBody  
  ```json    
  {    
     "specId": 1  
  }  
  ```    
	MyBatis-Plus will execute the following SQL  
  ```sql    
  SELECT * FROM pdt_spec    
  WHERE ( spec_id = 1)  ORDER BY create_time ASC    
```    



## 3. 注解介绍

### 3.1. @CustomerQuery

- 该注解用在实体类的属性上,用于标记对应字段的查询类型或排序。

#### 3.1.1. value
- 该参数用于设置对应字段的查询类型,默认为 QueryType.EQ。
- 示例:
```java
@TableField(value = "spec_name")  
@CustomerQuery(QueryType.LIKE)  
private String specName;
```

#### 3.1.2. QueryType.EQ
- 等于查询,与 MyBatis-Plus 的 eq 对应
#### 3.1.3. QueryType.NOT_EQUAL
- 不等于,与 MyBatis-Plus 的 ne 对应
#### 3.1.4. QueryType.LIKE
- LIKE '%值%',与 MyBatis-Plus 的 like 对应
#### 3.1.5. QueryType.NOT_LIKE
- NOT LIKE '%值%',与 MyBatis-Plus 的 notLike 对应
#### 3.1.6. QueryType.LIKE_LEFT
- LIKE '%值',与 MyBatis-Plus 的 likeLeft 对应
#### 3.1.7. QueryType.LIKE_RIGHT
- LIKE '值%',与 MyBatis-Plus 的 likeRight 对应
#### 3.1.8. QueryType.BETWEEN
- BETWEEN 值1 AND 值2,与 MyBatis-Plus 的 between 对应
#### 3.1.9. QueryType.NOT_BETWEEN
- NOT BETWEEN 值1 AND 值2,与 MyBatis-Plus 的 notBetween 对应
#### 3.1.10. QueryType.LESS_THAN
- 小于 <,与 MyBatis-Plus 的 lt 对应
#### 3.1.11. QueryType.LESS_EQUAL
- 小于等于 <=,与 MyBatis-Plus 的 le 对应
#### 3.1.12. QueryType.GREATER_THAN
- 大于 >,与 MyBatis-Plus 的 gt 对应
#### 3.1.13. QueryType.GREATER_EQUAL
- 大于等于 >=,与 MyBatis-Plus 的 ge 对应
#### 3.1.14. QueryType.IN
- 字段 IN (v0, v1, ...),与 MyBatis-Plus 的 in 对应
#### 3.1.15. QueryType.NOT_IN
- 字段 NOT IN (value.get(0), value.get(1), ...),与 MyBatis-Plus 的 notIn 对应
#### 3.1.16. QueryType.SQL
- SQL 语句,与 MyBatis-Plus 的 sql 对应

#### 3.1.17. orColumns
- 或查询,查询时匹配多个字段。
  例如在在查询规格表时,需要同时用一个参数模糊查询规格名和重量码。

- 示例
  实体类
```java
@TableName(value ="pdt_spec")  
@Data  
public class PdtSpec{
	/**  
	 * 
	 * id 
	 * */
	@TableId(value = "spec_id")  
	private Long specId;

	/**  
	 * 规格名  
	 */
	@TableField(value = "spec_name")  
	@CustomerQuery(QueryType.LIKE,,orColumns = {"weight_code"})  
	@Excel(name = "规格名")  
	private String specName;

	/**  
	 * 重量码  
	 */  
	@TableField(value = "weight_code")  
	@CustomerQuery(QueryType.LIKE)  
	@Excel(name = "重量码")  
	private String weightCode;

	/**  
	 * 商品状态（0正常 1停用 2 淘汰）  
	 */  
	@TableField(value = "status")  
	private Integer status;
}
```

查询参数

```
{
  "specName": "11",
}
```

执行 SQL

```sql
SELECT * FROM pdt_spec
 WHERE (spec_name LIKE '%11%' OR weight_code LIKE '%11%');
```

#### 3.1.18. columnName
- 字段名,用于指定对应属性在数据库表字段名,默认为空,优先级高于 @TableField 中 value。非必填,默认会从 mybatis 的缓存中取出属性值对应数据库表字段名。
- 示例
```java
@TableField(value = "spec_name_temp")
@CustomerQuery(value = QueryType.LIKE,columnName = "spec_name")
private String specName;
```

查询时执行 SQL
```sql
SELECT * FROM pdt_spec
 WHERE (spec_name LIKE '%小%') 
```

#### 3.1.19. @orderType
- 该字段用于排序,默认为 OrderType.NONE,不进行排序。
- 示例
  实体类
```java
	@TableName(value ="pdt_spec")  
	public class PdtSpec  {    
		/**  
		 * id 
		 * 
		 * */    
		@TableId(value = "spec_id")   
		private Long specId;    
		/**  
		 * 规格名  
		 */
		@CustomerQuery(value = QueryType.LIKE)   
		@TableField(value = "spec_name")   
		private Long specName;    
		/**  
		 * 创建时间 
		 */
		@CustomerQuery(orderType = OrderType.ASC)   
		@TableField(value = "create_time",orderPriority = 100)   
		private Date createTime;  
		/**  
		 * 商品状态（0正常 1停用 2 淘汰）  
		 */  
		@TableField(value = "status")  
		@CustomerQuery(orderType = OrderType.DESC,orderPriority = 111)
		private Integer status;	
	}    
```
查询参数

```
{
  "specName": "11",
}
```

执行 SQL

```sql
SELECT * FROM pdt_spec
 ORDER BY create_time ASC, status DESC;
```

#### 3.1.20. orderPriority
- 用于设置排序优先级,默认为 0。

#### 3.1.21. betweenStartVal
- 当查询类型是 BETWEEN 类型时, BETWEEN 值1 AND 值2,betweenStartVal 表示值1 的属性名,默认为 startTime。
- 示例
  查询 DTO
```java
@Data  
public class PdtSpecRequestDto extends PdtSpec {  
    /**  
     * 开始时间  
     */  
    private Date startTime;  
  
    /**  
     * 结束时间  
     */  
    private Date endTime;  
  
}
```

实体类
```java
	@TableName(value ="pdt_spec")  
	public class PdtSpec  {    
		/**  
		 * id 
		 * 
		 * */    
		@TableId(value = "spec_id")   
		private Long specId;    
		/**  
		 * 规格名  
		 */
		@CustomerQuery(value = QueryType.LIKE)   
		@TableField(value = "spec_name")   
		private Long specName;    
		/**  
		 * 创建时间 
		 */
		@CustomerQuery(orderType = OrderType.ASC)   
		@TableField(value = QueryType.BETWEEN,value = "create_time")   
		private Date createTime;  

	}    
```

查询参数

```
{
  "specName": "11",
}
```

执行 SQL

```sql
SELECT * FROM pdt_spec
 ORDER BY create_time ASC, status DESC;
```


#### 3.1.22. betweenEndVal
- 当查询类型是 BETWEEN 类型时, BETWEEN 值1 AND 值2,betweenEndVal 表示值 2 的属性名,默认为 endTime。

#### 3.1.23. notBetweenStartVal
- 当查询类型是 NOT_BETWEEN 类型时, BETWEEN 值1 AND 值2,notBetweenStartVal 表示值1 的属性名,默认为 startTime。
#### 3.1.24. notBetweenEndVal
- 当查询类型是 NOT_BETWEEN 类型时, BETWEEN 值1 AND 值2,notBetweenEndVal 表示值 2 的属性名,默认为 endTime。

#### 3.1.25. exist
- 当不希望字段参与查询时,将该值设置为 true 即可过滤。

#### 3.1.26. joinType
- 表连接(未实现)。

#### 3.1.27. joinEntityClass
- 表连接实体类(未实现)。

#### 3.1.28. sql
- 当查询类型是 SQL 类型时, 会将该参数拼接到 SQL 中,默认为空值。

### 3.2. @CustomerOrder
- 该注解用于实体类上,用于设置排序。不会与@CustomerQuery 中设置的排序冲突。
- 如果有字段重复排序,只会取最后识别的。

- 示例
```java
	@TableName(value ="pdt_spec")  
	public class PdtSpec  {    
		/**  
		 * id 
		 * 
		 * */    
		@TableId(value = "spec_id")   
		private Long specId;    
		/**  
		 * 规格名  
		 */
		@CustomerQuery(value = QueryType.LIKE)   
		@TableField(value = "spec_name")   
		private Long specName;    
		/**  
		 * 商品状态（0正常 1停用 2 淘汰）  
		 */  
		@TableField(value = "status")  
		@CustomerQuery(orderType = OrderType.DESC,orderPriority = 1111)  
		private Integer status;
		/**  
		 * 创建时间 
		 */
		@CustomerQuery(orderType = OrderType.ASC)   
		@TableField(value = QueryType.BETWEEN,value = "create_time")   
		private Date createTime;  

	}    
```

#### 3.2.1. orderColumnNames
- 排序的字段名,最好直接填入表字段名。
#### 3.2.2. orderTypes
- 排序类型
#### 3.2.3. orderColumn
- 开启排序,默认开启。

# License

MyBatis-Plus is under the Apache 2.0 license. See the Apache License 2.0 file for details.