# CompanyDataCrawler micro service
Rest service to fetch matching company data for user input


## Pre requisites
* sbt 1.3.13
* Java 1.8+
* Scala 2.13

#### Things covered in the application
* starting standalone HTTP server,
* handling file-based configuration,
* logging,
* routing,
* deconstructing requests,
* unmarshalling JSON entities and CSV file to Scala's case classes,
* marshaling Scala's case classes to JSON responses and CSV file responses,
* error handling,
* testing with mocking of external services.


## Usage

Start services with sbt:

```
$ sbt
> ~reStart
```

With the service up, you can start sending HTTP requests:

Test the service via sample GET JSON request as below
```
$ curl 'http://localhost:9000/company?name=Jesus&minThreshold=0.5'
{
  "id": "4047907",
  "name": "Jesus House",
  "score": 0.5
}
```

Below is the sample URL which accepts CSV file as input and fetches the matching company result in csv,
NOTE : Default minThreshold=0.0
```
curl -F 'userInputCsv=@{FILE_PATH}/sample_user_records.csv' 'http://localhost:9000/matchingcompanies' --output ~/{OUTPUT_PATH}/macthing_companies.csv

### Output CSV file should look like
id,name,matched_company,matched_company_id
1,DueDil Ltd,,
...
20,Jetstone Asset Management (UK),,
```

Sample URL to accept CSV file and minThreshold as input inorder to fetch matching company result in csv,
NOTE : Default minThreshold=1.0
```
curl -F 'userInputCsv=@{FILE_PATH}/sample_user_records.csv' 'http://localhost:9000/matchingcompanies?minThreshold=0.0' --output ~/{OUTPUT_PATH}/macthing_companies.csv

### Output CSV file should look like
id,name,matched_company,matched_company_id
1,DueDil Ltd,Foodstore Ltd,SC149256
...
20,Jetstone Asset Management (UK),Jetstone Asset Management (UK) LLP,OC394214
```

### Testing

Execute tests using `test` command:

```
$ sbt
> test
```

### Configuration (application.conf)
* http => defines host and port for the web server
* services => Database configuration
* companyFilePath => Path to companies.csv
* tmpDir => Temporary directory location for the server to dump temporary files.

Assumptions :
1. User inputs to the rest API will be valid and validation is not required for this version.
2. All the company CSV data can pre loaded and fit into memory.

Limitations :
1. Company is being loaded once and cannot be update until the next restart.
2. REST API accepts csv and outputs in form of CSV and does not support any other format
3. No user input validations or appropriate error messages for it.


Challenges :
* This is my first Akka web based application.  