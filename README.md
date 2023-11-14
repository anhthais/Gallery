# Note
Sử dụng theme tối để thấy chữ của dialog nha :)))
# Gallery Databse 
Như tui có đề cập trong tài liệu trên **Trello** thì cấu trúc của Database tương tự như vậy.

## Database Helper for Gallery App

The `DatabaseHelper` class is an integral part of Android Gallery App. It is responsible for managing the SQLite database that stores information about images, including their names, file addresses, album names, status, and time remaining. 

## Class Overview

- **Class Name**: `DatabaseHelper`
- **Database Name**: GalleryDB.db
- **Database Version**: 1

## Table Structure

The database contains a single table named "Images" with the following columns:

1. `ID` - Primary Key (Auto-incremented)
2. `Name` - Name of the image
3. `Address` - Image file address
4. `Album_Name` - Name of the album
5. `Status` - Status of the image (có sự thay đổi tên cột từ State thành status)
6. `Time_Remaining` - Remaining time

## Constructor

### `DatabaseHelper(Context context)`

- **Description**: Creates a new instance of the `DatabaseHelper` class.
- **Parameters**:
  - `context`: The context of the Android application.

## Database Operations

### `addImage(String name, String address, String album_name, String status, String timeRemaining)`

- **Description**: Inserts a new image into the "Images" table.
- **Parameters**:
  - `name`: Name of the image.
  - `address`: File address of the image.
  - `album_name`: Name of the album.
  - `status`: Status of the image.
  - `timeRemaining`: Remaining time for the image.
- **Usage**:
  ```java
  DatabaseHelper databaseHelper = new DatabaseHelper(context);
  databaseHelper.addImage("ImageName", "ImagePath", "AlbumName", "Status", "TimeRemaining");
  ```

### `readAllData()`

- **Description**: Retrieves all data from the "Images" table.
- **Returns**: A `Cursor` containing all data from the table.
- **Usage**:
  ```java
  DatabaseHelper databaseHelper = new DatabaseHelper(context);
  Cursor cursor = databaseHelper.readAllData();
  // Process the cursor to retrieve image data.
  ```

### `updateData(String row_id, String name, String address, String album_name, String status, String timeRemaining)`

- **Description**: Updates data for a specific image in the "Images" table.
- **Parameters**:
  - `row_id`: The ID of the image to update.
  - `name`: Updated name of the image.
  - `address`: Updated file address of the image.
  - `album_name`: Updated album name.
  - `status`: Updated status of the image.
  - `timeRemaining`: Updated remaining time for the image.
- **Usage**:
  ```java
  DatabaseHelper databaseHelper = new DatabaseHelper(context);
  databaseHelper.updateData("ImageID", "UpdatedName", "UpdatedAddress", "UpdatedAlbumName", "UpdatedStatus", "UpdatedTimeRemaining");
  ```

### `deleteOneRow(String row_id)`

- **Description**: Deletes a specific image from the "Images" table.
- **Parameters**:
  - `row_id`: The ID of the image to delete.
- **Usage**:
  ```java
  DatabaseHelper databaseHelper = new DatabaseHelper(context);
  databaseHelper.deleteOneRow("ImageID");
  ```

## Important Note

- Ensure that you do not use spaces within column names or table names, as it can lead to errors in SQL queries.

- Handle database operations (insert, read, update, delete) in your application's activity or fragment as needed.

This `DatabaseHelper` class provides essential functionality for managing image data in your Android Gallery App. You can use it to store, retrieve, update, and delete image information in the database.

## Resource Helper for implement feature
- Video how to implement these method I have founded in Youtube :
[BookManagerApp_using SQLite](https://www.youtube.com/watch?v=J-CP7g_GwpI&list=PLSrm9z4zp4mGK0g_0_jxYGgg3os9tqRUQ&index=6)

- This video also contents how to download Browser for SQLite make sure you watch it
Link download this : [DB.Brower.for.SQLite](https://sqlitebrowser.org/)
