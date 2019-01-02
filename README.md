# money-transfer


Simple money transferring system implementation with exposed REST API, backed by in-memory database.

Technologies used:
[`Slick`][slick], [`H2`][h2], [`GenCodec`][gencodec], [`ScalaTest`][scalatest]

Application is using [`SBT`][sbt]

For running the application: ```sbt run```.
To create a jar: ```sbt assembly```.

The API is by default at: [http://localhost:8081/](http://localhost:8081/) and exposes following endpoints:

# ```ACCOUNTS```

## ```GET /accounts```
  which returns all accounts.

  Sample response
  ```
  [{"balance":0.00,"id":1},{"balance":100.00,"id":2},{"balance":200.00,"id":3},{"balance":300.00,"id":4}]
  ```

  Possible response with status `404 Not Found`

## ```GET /accounts/{id}```
which returns an account by id

Sample response
```
{"balance":0.00,"id":1}
```

## ```GET /accounts/{id}/transactions```
which returns transaction that affected the account.

Sample response:
```
[{"from":2,"to":3,"amount":2.00,"timestamp":"2019-01-02T20:35:41.673Z","id":3},{"from":3,"to":4,"amount":3.00,"timestamp":"2019-01-02T20:35:41.673Z","id":4}]
```


## ```POST /accounts```
which creates new account and returns its id.

Sample request:
```
{
	"initialBalance": 200.31
}
```

Sample response:
```
5
```

Possible response with status `400 Bad Request`
```
Account balance must be negative but was -20
```

# ```TRANSACTIONS```


## ```GET /transactions```
which return all transactions.

Sample response:
```
[{"from":0,"to":1,"amount":100.00,"timestamp":"2019-01-02T20:46:15.178Z","id":1},{"from":1,"to":2,"amount":200.00,"timestamp":"2019-01-02T20:46:15.182Z","id":2},{"from":2,"to":3,"amount":300.00,"timestamp":"2019-01-02T20:46:15.182Z","id":3},{"from":3,"to":4,"amount":400.00,"timestamp":"2019-01-02T20:46:15.182Z","id":4}]
```

## ```POST /transaction```
which executes and persists provided transaction.

Sample request:
```
{
	"from": "2",
	"to": "3",
	"amount": "20.50"
}
```

Sample response:
```
5
```

Possible responses with status `400 Bad Request`
```
Source account with id 777 does not exist
```
```
Target account with id 777 does not exist
```
```
Source account with id 888 and target account with id 777 do not exist
```
```
Transaction amount -10000.00 must be positive
```
```
Account with id 1 does not have enough funds to transfer 10000.00
```



[slick]: https://github.com/slick/slick
[h2]: https://github.com/h2database/h2database
[gencodec]: https://github.com/AVSystem/scala-commons/blob/master/docs/GenCodec.md
[scalatest]: https://github.com/scalatest/scalatest
[sbt]: https://github.com/sbt/sbt

