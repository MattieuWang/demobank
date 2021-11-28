# demobank
This dummy bank project provides serveral actions: 
1. check the status information of my account.
2. deposit amount of money.
3. withdraw amount of money.
4. check all the operations
5. create a new account
6. log in / log out.

The server will maintain only one session of the user per IP address. And will log out automatically after 2 minutes without any operation. 
There will be a jwt cookie saved on the client side and the server will check the cookie with the internal session for every request. 
Every operation will be recorded and logged in console.

# Instructions
1. To create a new user account in the server, you need to POST localhost:8080/auth/user with the JSON below:
```
{
    "username": "aaa",
    "password": "ttt"
}
```
if success, it will return a ramdom user id and the username.
```
{
    "id": "73045341-4cdd-4784-a0ea-472d10c779eb",
    "username": "aaa"
}
```
The server only accepts one user account with the same username.

2.  To check the account status: GET localhost:8080/user
if success, it will return the information:
```
{
  "balance" : 0.0,
  "id" : "73045341-4cdd-4784-a0ea-472d10c779eb",
  "username" : "aaa"
}
```
3.  To deposit or withdraw, you can use POST localhost:8080/user/deposit or POST localhost:8080/user/withdraw, with the JSON below.
It must contain a positive number with a demical
```
{
    "amount" : 300.0
}
```
If success, it will return a receipt:
```
{
    "user_id": "73045341-4cdd-4784-a0ea-472d10c779eb",
    "username": "aaa",
    "amount": 300.0,
    "balance": 600.0,
    "msg": "Success",
    "op": "DEPOSIT"
}
```
4.  To check the operation records: GET localhost:8080/user/operations which will return a list of operations
5.  To login: POST localhost:8080/auth/login, with the JSON below
```
{
    "username": "aaa",
    "password": "ttt"
}
```
6.  To log out: POST localhost:8080/auth/logout

7.  If the current user session is over, you can not do any user operation (check status, deposit, withdraw, check operations) before log in again. Otherwise it will return :
```
{
    "code": "401",
    "message": "You're not authorized"
}
```
