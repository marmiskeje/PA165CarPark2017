# REST API [WIP]

Rest api is availabe on: http://localhost:8080/pa165/rest

## COMMANDS

# Car
Get all Cars: 
```
curl -X GET http://localhost:8080/pa165/rest/car
```
Get all Cars in branch: 
```
curl -X GET http://localhost:8080/pa165/rest/car?branchId=1
```
Get all deactivated cars: 
```
curl -X GET http://localhost:8080/pa165/rest/car?activated=false
```
Get all deactivated cars in particular branch: 
```
curl -X GET http://localhost:8080/pa165/rest/car?activated=false&branchId=1
```
Find car by ID: 
```
curl -X GET http://localhost:8080/pa165/rest/car/1
```
Create new car: 
```
curl -X POST -i -H "Content-Type: application/json" --data ' http://localhost:8080/pa165/rest/car
data: {"name": "Audi A3"}
```
Update car: 
```
curl -X PUT -i -H "Content-Type: application/json" --data ' http://localhost:8080/pa165/rest/car
data: {"id": 1, "name": "Audi A3"}
```
Deactivate car (with id 1):
```
curl -X DELETE http://localhost:8080/pa165/rest/car/1
```

# Reservation

Get all reservations without denied: 
```
curl -X GET http://localhost:8080/pa165/rest/reservation
```
Get all reservations with denied: 
```
curl -X GET http://localhost:8080/pa165/rest/reservation?ignoreDenied=false
```
Find reservation by ID: 
```
curl -X GET http://localhost:8080/pa165/rest/reservation/1
```
Create new reservation: 
```
curl -X POST -i -H "Content-Type: application/json" --data ' http://localhost:8080/pa165/rest/reservation
data: {
             "name": "reservation",
             "car" : {
             	"id": 1,
             	"name": "Audi A3"
             },
             "user" : {
             	"id": 1,
                 "userName": "Mato",
                 "type": "BRANCH_MANAGER"
             },
             "reservationStartDate" : "2017-12-17T21:25:47.277",
             "reservationEndDate" : "2016-11-17T21:25:47.277",
             "state" : "CREATED"
         }
```
Update reservation: 
```
curl -X PUT -i -H "Content-Type: application/json" --data ' http://localhost:8080/pa165/rest/reservation
data: {
      	"id": 4,
          "name": "reservation2",
           "car" : {
            "id": 1,
            "name": "Audi A3"
           },
           "user" : {
            "id": 1,
               "userName": "Mato",
               "type": "BRANCH_MANAGER"
           },
           "reservationStartDate" : "2017-12-17T21:25:47.277",
           "reservationEndDate" : "2016-11-17T21:25:47.277",
          "state" : "APPROVED"
      }
```
Find all reservation for particular branch (id is as parameter, time period is in the json): 
```
curl -X POST -i -H "Content-Type: application/json" --data ' http://localhost:8080/pa165/rest/reservation/1
data: 
{
    "start" : "2017-12-18T20:37:52.862",
    "end" : "2017-12-19T20:37:52.862"
}
```

Find all reservation for particular branch and children (add children param to true, default is set to false) except denied: 
```
curl -X POST -i -H "Content-Type: application/json" --data ' http://localhost:8080/pa165/rest/reservation/1?children=true
data: 
{
    "start" : "2017-12-18T20:37:52.862",
    "end" : "2017-12-19T20:37:52.862"
}
```
Find all reservation for particular branch (id is as parameter, time period is in the json) with denied: 
```
curl -X POST -i -H "Content-Type: application/json" --data ' http://localhost:8080/pa165/rest/reservation/1?ignoreDenied=false
data: 
{
    "start" : "2017-12-18T20:37:52.862",
    "end" : "2017-12-19T20:37:52.862"
}

```
Find all reservation for particular user except denied:
```
curl -X POST -i -H "Content-Type: application/json" --data ' http://localhost:8080/pa165/rest/reservation/1/user/3
data: 
{
    "start" : "2017-12-18T20:37:52.862",
    "end" : "2017-12-19T20:37:52.862"
}
```
Find all reservation for particular user with denied:
```
    curl -X POST -i -H "Content-Type: application/json" --data ' http://localhost:8080/pa165/rest/reservation/1/user/3?ignoreDenied=false
    data: 
    {
        "start" : "2017-12-18T20:37:52.862",
        "end" : "2017-12-19T20:37:52.862"
    }
```
# Branch

Get all branches: 
```
curl -X GET http://localhost:8080/pa165/rest/branch
```
Find branch by ID: 
```
curl -X GET http://localhost:8080/pa165/rest/branch/1
```
Create new branch: 
```
curl -X POST http://localhost:8080/pa165/rest/branch
data: { "name" : "testBranch" }
```
Update branch: 
```
curl -X PUT -i -H "Content-Type: application/json" --data ' http://localhost:8080/pa165/rest/branch
data: { "id" : 3, "name" : "testBranch2"}
```
Assign user 
```
curl -X PUT -i -H "Content-Type: application/json" --data ' http://localhost:8080/pa165/rest/branch/3/assignUser
data: { "id": 1, "userName": "Mato" }
```
Assign car 
```
curl -X PUT -i -H "Content-Type: application/json" --data ' http://localhost:8080/pa165/rest/branch/3/assignCar
data: { "id": 1, "name": "Audi A3" }
```
Find all available car for branch 
```
curl -X PUT -i -H "Content-Type: application/json" --data ' http://localhost:8080/pa165/rest/branch/3/findAvailable
data: { "date" : "2017-12-17T23:49:12.86" }
```

# User

Get all users: 
```
curl -X GET http://localhost:8080/pa165/rest/user
```
Authentification user:
```
curl -X POST http://localhost:8080/pa165/rest/user/authenticate
data: { 
"userName" : "admin",
"password" : "admin" 
}
```




[WIP]
