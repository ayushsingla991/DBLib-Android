# DBLib-Android
A simple SQLite Database Library for Android.

#Usage
Use as dependency inbuild.gradle
```
com.ayushsingla.dblib:dblib:1.0.0
```

# Initialising and creating a Database:
`DBHandler db = new DBHandler(context, "DATABASE_NAME", "1"); // Last argument is database version`

# Creating a table:<br/>
You can create table using hashmap or a simple array.<br/><br/>

* Using Hashmap:<br/>
```
HashMap<String, String> columns = new HashMap<>();
columns.put("rowId", "INTEGER PRIMARY KEY"); // key should be column name and value should be datatype of the column
columns.put("name", "TEXT");
columns.put("age", "INTEGER");
columns.put("city", "TEXT");
db.createTable("TABLE_NAME", columns); // first argument is table name second is hashmap
```
<br/><br/>

* Using Array:<br/>
```
db.createTable("TABLE_NAME", new String[]{"rowId", "INTEGER PRIMARY KEY", "name", "TEXT", "age", "INTEGER", "city", "TEXT"}); //column name and datatype should be on alternate positions in array
```
<br/>
every time you creates a table this function check if it is already exists or not.<br/><br/>

# Saving Data into table:<br/>
```
HashMap<String, String> data = new HashMap<>();
data.put("name", name);
data.put("age", age);
data.put("city", city);
db.insertRow("TABLE_NAME", data);
```
OR
```
db.insertRow("TABLE_NAME", new String[]{"name", name, "age", age, "city", city});
```
<br/><br/>

# Update data into table:<br/>
```
HashMap<String, String> data = new HashMap<>();
data.put("name", "new Name");
data.put("age", "15");
data.put("city", "new city");

HashMap<String, String> condition = new HashMap<>();
condition.put("rowId", "8"); // this is condition where column rowId = 8
db.updateRows(DB.TABLE_NAME, data, condition);
```
OR
```
HashMap<String, String> data = new HashMap<>();
data.put("name", "new Name");
data.put("age", "15");
data.put("city", "new city");
db.updateRows(DB.TABLE_NAME, data, new String[]{"rowId", "8"});
```
<br/><br/>

#Fetch data from table<br/><br/>

**Function 1:** for fetching all rows and all columns<br/>
```
List<HashMap<String, String>> data = new ArrayList<>();
data = db.getRowsWhereWithAnd("TABLE_NAME", new String[]{"*"}, new String[]{}, 4);
```
OR
```
HashMap<String, String> condition = new HashMap<>();
data = db.getRowsWhereWithAnd("TABLE_NAME", new String[]{"*"}, condition, 4);
```
<br/>
//First Argument-> Table Name<br/>
//Second Argument-> columns [This can be column name as weel. You will see the example below]<br/>
//Third Argument-> Condition [We are fetching all rows so there is no condition, hence pass empty array]<br/>
//Fourth Argument-> number of columns to be fetched<br/><br/><br/>
**Function 2:** for fetching all columns on a particular condition<br/>
```
data = db.getRowsWhereWithAnd("TABLE_NAME", new String[]{"*"}, new String[]{"age", "20"}, 4); // column and values are on alternate positions.
```
OR
```
HashMap<String, String> condition = new HashMap<>();
condition.put("age", "20");
data = db.getRowsWhereWithAnd("TABLE_NAME", new String[]{"*"}, condition, 4);
```
OR (for fetching with custom condition, this will work only with arrays)
```
data = db.getRowsWhereWithAnd("TABLE_NAME", new String[]{"*"}, new String[]{"age > 20"}, 4);
```
<br/><br/>

**Function 3:** for fetching selected columns with any condition or not<br/>
```
data = db.getRowsWhereWithAnd("TABLE_NAME", new String[]{"name", "age"}, new String[]{"age > 20"}); // number of rows not required while specifying the required columns
```
OR
```
HashMap<String, String> condition = new HashMap<>();
condition.put("age", "20");
data = db.getRowsWhereWithAnd("TABLE_NAME", new String[]{"name", "age"}, condition);
```
<br/><br/>
#Execute custom query with no results (ex: update)
```
db.executeQuery("UPDATE TABLE_NAME SET city = \'chandigarh\' WHERE age >= 20");
```
<br/><br/>
#Execute custom query and fetch data in arraylist
```
data = db.runQueryGetResult("SELECT * FROM TABLE_NAME WHERE age > 20 and name = \'ayush\'", 4);
```
<br/><br/>
###NOTE:<br/>
######Every query which reutrn arraylist of hashmap will return the column name as a key in their respective maps


