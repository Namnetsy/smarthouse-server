# Smarthouse server
This is a back-end part of a bigger project written in Java using [Javalin](https://javalin.io) framework.

In short the server should provide an API gateway for our android app and at the same time keep a constant connection with our Arduino.

Our android app should be able to do these things:
 - sign up/in
 - add hub(Arduino)
 - add members to it's hub
 - manage members(remove members, leave it's hub to a member)
 - change some info(username, email, password, FCM token)
 - send commands to it's hub
 - session management(removing, listing all logged in devices)

Our hubs should be able to do these things:
 - authenticate  
 - receive commands from it's owner

Also the server side does these things:  
 - sends email letter with session info and a revoke link that expires after 5 minutes upon each user authentication
 - logs each request in JSON format

## Disclaimer
I erased the commit history, because all of a sudden I committed my credentials :(

But you're lucky because I saved you from reading my bad written commit messages :)

PS: I did committed my credentials again a few times before finally committing this :(

## Rest API gateway
All the docs are available [here](https://studio-ws.apicur.io/sharing/05a8c4fd-da75-41f4-8de5-99a28d249f67).

## Arduino API gateway
I used websockets for keeping a constant connection, but the implementation in itself is real bad, cause tokens for authenticating Arduino were stored in a relational database which was queried on each request all the time without any caching mechanism!

There's no docs available, but just know it's for the better :)

## Architecture

### Authentication/Authorization
Opaque tokens with expiry time which resets upon every use.

### Databases
I used two databases, relational db for storing static data such as user info, hubs, members, etc.
And the second one is a very primitive NoSQL in-memory database called Redis for storing user tokens, temporary email verification codes, sessions & session revoke links.

### Why this way?
Who knows, I knew nothing about how APIs work back then when I got started.
