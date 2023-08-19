## Examples of REST Operations


### Fetch a Single Object
Get a single object using GET and its id:
```bash
curl -verbose http://localhost:8089/api/person/1
```
Resposne:
```bash
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 19 Aug 2023 15:05:40 GMT

{"id":1,"lastName":"Smith","firstName":"Fred","address":"232 Oak St.",
"city":"Metropolis","state":"OA","zipCode":"555444"}
```

### Get All of the Objects
Get all objects by using GET:
```bash
curl -verbose http://localhost:8089/api/person
```
Resposne:
```bash
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 19 Aug 2023 15:10:22 GMT

[{"id":1,"lastName":"Smith","firstName":"Fred","address":
"232 Oak St.","city":"Smallville","state":"OA","zipCode":"555444"},
{"id":2,"lastName":"Cooper","firstName":"Sam","address":"111 Main St.",
"city":"Littleton","state":"GS","zipCode":"99001"}]
```
### Create a New Object
Create a new object using POST
```bash
curl -H "Content-Type: application/json" \
-X POST http://localhost:8089/api/person  \
-d '{"firstName":"Fred","lastName":"Smith","address":"232 Oak St.",\
"city":"Smallville", "state":"OA","zipCode":"555444"}'
```

### Update an Existing Object
Update (or create if it doesn't exist) an object using PUT:
```bash
curl -H "Content-Type: application/json" \
-X PUT http://localhost:8089/api/person/1  \
-d '{"firstName":"Fred","lastName":"Smith","address":"777 Dark Ave.",\
"city":"Middletown", "state":"MC","zipCode":"01122"}'
```
### Delete an Object
Delete an object by  its id using DELETE:
```bash
curl -verbose -X DELETE http://localhost:8089/api/person/2
```
Response:
```bash
HTTP/1.1 204 
Date: Sat, 19 Aug 2023 21:04:37 GMT
```
### Replace a Field in an Object
Update one or more fields in an object 
by using a PATCH request with a patch format request:
```bash
curl -H "Content-Type: application/json-patch+json" \
-X PATCH http://localhost:8089/api/person/1 \
-d '[{"op":"replace","path":"/city","value":"Metropolis"}, \
{"op":"replace","path":"/state","value":"VM"}, \
{"op":"replace","path":"/zipCode","value":"77655"}]'
```

