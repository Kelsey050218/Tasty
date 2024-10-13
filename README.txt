Controller: CommonController
Method: POST
Endpoint: upload
Description: Upload and store recipe pictures to Alibaba Cloud
Body Parameters:
file: string
Responses:
code": 0,
  "msg: 

---------------------------------------
Controller: RecipeController
Method: POST
Endpoint: search
Description: Search recipe dynamic SQL query according to menu, type, people, time, calories
Body Parameters:
{
  "menu": "string",
  "type": "string",
  "people": 0,
  "time": 0,
  "calories": 0
}
Responses:
menu: string
code": 0,
  "msg: 

---------------------------------------

Controller: RecipeController
Method: GET
Endpoint: recommend
Description: Recommended daily recipes
Body Parameters:
{
  "code": 0,
  "msg": "",
  "data": [
    {
      "recipeId": 0,
      "menu": "",
      "type": "",
      "people": 0,
      "time": 0,
      "calories": 0,
      "recipePicture": "",
      "score": 0,
      "name": ""
    }
  ]
}
Responses:
code": 0,
  "msg: 

---------------------------------------

Controller: RecipeController
Method: GET
Endpoint: getById
Description: Look up a recipe based on its id
Body Parameters:
{
  "code": 0,
  "msg": "",
  "data": {
    "recipeId": 0,
    "menu": "",
    "type": "",
    "people": 0,
    "time": 0,
    "calories": 0,
    "recipePicture": "",
    "score": 0,
    "name": "",
    "guide": {
      "id": 0,
      "recipeId": 0,
      "steps": ""
    },
    "ingredients": [
      {
        "id": 0,
        "recipeId": 0,
        "picture": ""
      }
    ]
  }
}
Responses:
code": 0,
  "msg: 

---------------------------------------

Controller: RecipeController
Method: POST
Endpoint: collect
Description: Bookmark recipes and store associated information in a database
Body Parameters:
{
  "code": 0,
  "msg": "",
  "data": ""
}
Responses:
code": 0,
  "msg: 

---------------------------------------

Controller: RecipeController
Method: GET
Endpoint: getCollect
Description: Query recipes that have been collected by users according to the database favorites and recipe id tables
Body Parameters:
{
  "code": 0,
  "msg": "",
  "data": [
    {
      "recipeId": 0,
      "menu": "",
      "type": "",
      "people": 0,
      "time": 0,
      "calories": 0,
      "recipePicture": "",
      "score": 0,
      "name": ""
    }
  ]
}
Responses:
code": 0,
  "msg: 

---------------------------------------

Controller: UserServiceController
Method: POST
Endpoint: Login
Description: The user logs in, and the jwt token is added to record the login status
Body Parameters:
{
  "password": "string",
  "username": "string"
}
Responses:
password: string
code": 0,
  "msg: 

---------------------------------------

Controller: UserServiceController
Method: POST
Endpoint: register
Description: The user registers an account and adds the user information to the database by sending a verification code.
Body Parameters:
{
  "userName": "string",
  "password": "string",
  "phone": "string"
}
Responses:
userName: string
code": 0,
  "msg: 

---------------------------------------

Controller: UserServiceController
Method: GET
Endpoint: registerVerifyCode
Description: Check registration code
Body Parameters:
{
  "code": 0,
  "msg": "",
  "data": {
    "info": "",
    "code": ""
  }
}
Responses:
code": 0,
  "msg: 

---------------------------------------

Controller: UserServiceController
Method: POST
Endpoint: logout
Description: Log out and delete your personal information stored in redis
Body Parameters:
{
  "code": 0,
  "msg": "",
  "data": ""
}
Responses:
code": 0,
  "msg: 

---------------------------------------

Controller: UserServiceController
Method: GET
Endpoint: getById
Description: Query personal information of a user based on its id
Body Parameters:
{
  "code": 0,
  "msg": "",
  "data": {
    "username": "",
    "sex": "",
    "place": "",
    "resume": "",
    "portrait": ""
  }
}
Responses:
code": 0,
  "msg: 

---------------------------------------

Controller: UserServiceController
Method: POST
Endpoint: forget
Description: If a user forgets the password, use the verification code to change the password.
Body Parameters:
{
  "phone": "string",
  "newPassword": "string"
}
Responses:
phone: string
code": 0,
  "msg: 

---------------------------------------

Controller: UserServiceController
Method: GET
Endpoint: forgetVerifyCode

GET /user/forgetVerifyCode
Description: Verification Forgot the password verification code
Body Parameters:
{
  "code": 0,
  "msg": "",
  "data": {
    "info": "",
    "code": ""
  }
}
Responses:
code": 0,
  "msg: 

---------------------------------------

Controller: UserServiceController
Method: GET
Endpoint: getMotivation
Description: Push a different motivational statement every day
Body Parameters:
{
  "code": 0,
  "msg": "",
  "data": ""
}
Responses:
code": 0,
  "msg: 

---------------------------------------

Controller: NoteController
Method: POST
Endpoint: addNote
Description: Add and upload new notes, including photos, recipes, descriptions and other basic information
Body Parameters:
{
  "notePicture": "string",
  "describe": "string",
  "recipe": {
    "recipeId": 0,
    "menu": "string",
    "type": "string",
    "people": 0,
    "time": 0,
    "calories": 0,
    "recipePicture": "string",
    "score": 0,
    "name": "string"
  }
}
Responses:
notePicture: string
code": 0,
  "msg: 

---------------------------------------

Controller: NoteController
Method: DELETE
Endpoint: delete
Description: Delete notes under this user id
Body Parameters:
{
  "code": 0,
  "msg": "",
  "data": ""
}
Responses:
code": 0,
  "msg: 

---------------------------------------

Controller: NoteController
Method: GET
Endpoint: NotePageQuery
Description: Use the pagehelper plugin to display notes by page count
Body Parameters:
{
  "code": 0,
  "msg": "",
  "data": {
    "total": 0,
    "records": [
      {}
    ]
  }
}
Responses:
code": 0,
  "msg: 

---------------------------------------

Controller: NoteController
Method: GET
Endpoint: myNotes
Description: Get all the user's own notes
Body Parameters:
{
  "code": 0,
  "msg": "",
  "data": {
    "total": 0,
    "records": [
      {}
    ]
  }
}
Responses:
code": 0,
  "msg: 
---------------------------------------

Controller: NoteController
Method: GET
Endpoint: getNoteById
Description: Get some detailed note information, such as production steps, ingredient charts, favorites
Body Parameters:
{
  "code": 0,
  "msg": "",
  "data": {
    "notePicture": "",
    "describe": "",
    "recipe": {
      "recipeId": 0,
      "menu": "",
      "type": "",
      "people": 0,
      "time": 0,
      "calories": 0,
      "recipePicture": "",
      "score": 0,
      "name": ""
    },
    "noteTime": "",
    "comments": [
      {
        "id": 0,
        "noteId": 0,
        "userId": 0,
        "commentTime": "",
        "remark": ""
      }
    ],
    "likeNum": 0
  }
}
Responses:
code": 0,
  "msg: 

---------------------------------------

Controller: NoteController
Method: POST
Endpoint: comment
Description: Comments correspond to notes, and the relationship between the user and the notes is stored in the database
Body Parameters:
{
  "remark": "string",
  "noteId": 0
}
Responses:
remark: string
code": 0,
  "msg: 

---------------------------------------

Controller: NoteController
Method: POST
Endpoint: like
Description: Realize the "like" function of notes
Body Parameters:
{
  "code": 0,
  "msg": "",
  "data": ""
}
Responses:
code": 0,
  "msg: 

---------------------------------------

Controller: NoteController
Method: GET
Endpoint: getLike
Description: Gets the notes that the user previously liked
Body Parameters:
{
  "code": 0,
  "msg": "",
  "data": [
    {
      "noteId": 0,
      "noteUserId": 0,
      "notePicture": "",
      "describe": "",
      "recipeId": 0,
      "noteTime": ""
    }
  ]
}
Responses:
code": 0,
  "msg: 

---------------------------------------

