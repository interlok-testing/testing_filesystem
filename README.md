# File System Testing

[![license](https://img.shields.io/github/license/interlok-testing/testing_filesystem.svg)](https://github.com/interlok-testing/testing_filesystem/blob/develop/LICENSE)
[![Actions Status](https://github.com/interlok-testing/testing_filesystem/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/interlok-testing/testing_filesystem/actions/workflows/gradle-build.yml)

Project tests interlok-varsub & interlok-filesystem features

## What it does

This project contains various workflows that make up a filesystem API.

Each workflow uses variables that can be found in \src\main\interlok\config\variables.properties
```
test-base-directory-path=C:\\workspace\\dev-testing\\filesystem-testing\\src\\test\\resources
test-folder=messages
test-folder-path=${test-base-directory-path}\\${test-folder}
test-base-directory-path-url=file://C:/workspace/dev-testing/filesystem-testing/src/test/resources
```

## Getting started

* `./gradlew clean build`
* `(cd ./build/distribution && java -jar lib/interlok-boot.jar)`

## directory-entity-service api

This workflow API allows you to get information from a jira ticket json file.

![workflow diagram](/check-api.png "directory-entity-service api workflow diagram")
  
The workflow is setup to use the variable.properties
```
test-base-directory-path=C:\\workspace\\dev-testing\\filesystem-testing\\src\\test\\resources
test-folder=messages
test-folder-path=${test-base-directory-path}\\${test-folder}
```

So when you query the api, you pass in which jira ticket number you want, and this relates to a file in the test-folder-path folder.
```
/c/workspace/dev-testing/filesystem-testing/src/test/resources/messages
$ ls
INTERLOK-3542.json INTERLOK-3551.json INTERLOK-3559.json INTERLOK-3567.json INTERLOK-3575.json INTERLOK-3583.json INTERLOK-3599.json 
INTERLOK-3543.json INTERLOK-3552.json INTERLOK-3560.json INTERLOK-3568.json INTERLOK-3576.json INTERLOK-3585.json INTERLOK-3600.json
INTERLOK-3545.json INTERLOK-3555.json INTERLOK-3561.json INTERLOK-3569.json INTERLOK-3577.json INTERLOK-3587.json INTERLOK-3704.json
INTERLOK-3546.json INTERLOK-3556.json INTERLOK-3563.json INTERLOK-3570.json INTERLOK-3579.json INTERLOK-3588.json INTERLOK-3705.json
INTERLOK-3547.json INTERLOK-3557.json INTERLOK-3564.json INTERLOK-3571.json INTERLOK-3581.json INTERLOK-3590.json INTERLOK-3707.json
INTERLOK-3548.json INTERLOK-3558.json INTERLOK-3566.json INTERLOK-3574.json INTERLOK-3582.json INTERLOK-3591.json INTERLOK-3708.json
INTERLOK-3710.json
```

the api uri:
* /check

the parameters:
* field //which field are you after
* ticket //which ticket

fields values available:
* "id"
* "createdAt"
* "updatedAt"
* "size"
* "parentDirectory"
* "absolutePath"

```
$ curl -s "http://localhost:8080/check?field=updatedAt&ticket=INTERLOK-3542"
2021-02-22T16:28:42+0000
```

```
$ curl -s "http://localhost:8080/check?field=size&ticket=INTERLOK-3551"
167
```

```
$ curl -s "http://localhost:8080/check?field=createdAt&ticket=INTERLOK-3555"
2021-02-22T16:28:42+0000
```

```
$ curl -s "http://localhost:8080/check?field=absolutePath&ticket=INTERLOK-3557"
C:\workspace\dev-testing\filesystem-testing\src\test\resources\messages\INTERLOK-3557.json
```

## directory-listing-service api

This workflow API allows you to get directory listings data.

![workflow diagram](/list-api.png "directory-listing-service api workflow diagram")

The actual directory-listing-service has the following config
```xml
  <directory class="constant-data-input-parameter">
    <value>${test-base-directory-path}\%message{folder}</value>
  </directory>
```

meaning we will be listing directories from a variable in variable.properties and a url property
```
test-base-directory-path=C:\\workspace\\dev-testing\\filesystem-testing\\src\\test\\resources
```

the api uri:
* /list

the parameters:
* folder //the folder within ${test-base-directory-path} that we will be listing

```
$ curl -s "http://localhost:8080/list?folder=messages"
```

response:
```
{
   "files":[
      "INTERLOK-3542.json",
      "INTERLOK-3543.json",
      "INTERLOK-3545.json",
      "INTERLOK-3546.json",
      "INTERLOK-3547.json",
      "INTERLOK-3548.json",
      "INTERLOK-3551.json",
      "INTERLOK-3552.json",
      "INTERLOK-3555.json",
      "INTERLOK-3556.json",
      "INTERLOK-3557.json",
      "INTERLOK-3558.json",
      "INTERLOK-3559.json",
      "INTERLOK-3560.json",
      "INTERLOK-3561.json",
      "INTERLOK-3563.json",
      "INTERLOK-3564.json",
      "INTERLOK-3566.json",
      "INTERLOK-3567.json",
      "INTERLOK-3568.json",
      "INTERLOK-3569.json",
      "INTERLOK-3570.json",
      "INTERLOK-3571.json",
      "INTERLOK-3574.json",
      "INTERLOK-3575.json",
      "INTERLOK-3576.json",
      "INTERLOK-3577.json",
      "INTERLOK-3579.json",
      "INTERLOK-3581.json",
      "INTERLOK-3582.json",
      "INTERLOK-3583.json",
      "INTERLOK-3585.json",
      "INTERLOK-3587.json",
      "INTERLOK-3588.json",
      "INTERLOK-3590.json",
      "INTERLOK-3591.json",
      "INTERLOK-3599.json",
      "INTERLOK-3600.json",
      "INTERLOK-3704.json",
      "INTERLOK-3705.json",
      "INTERLOK-3707.json",
      "INTERLOK-3708.json",
      "INTERLOK-3710.json"
   ]
}
```

## move-file-service api

This workflow API allows you to move a directory.

![workflow diagram](/move-api.png "move-file-service api workflow diagram")

The actual move-file-service has the following config
```xml
<move-file-service>
  <original-path>${test-base-directory-path}\%message{folder}</original-path>
  <new-path>${test-base-directory-path}\%message{newfolder}</new-path>
  <move-directory>true</move-directory>
</move-file-service>
```

the api uri:
* /move

the parameters:
* folder //the folder within ${test-base-directory-path} that we want to move
* newfolder //the new destination with ${test-base-directory-path} that we want to target

move 'test-folder' to 'test-folder2':
```
$ curl -s "http://localhost:8080/move?folder=test-folder&newfolder=test-folder2"
move directory success
```

check 'test-folder' contents (there should be none)
```
$ curl -s "http://localhost:8080/list?folder=test-folder"
null
```

check the new folder 'test-folder2' contents
```
$ curl -s "http://localhost:8080/list?folder=test-folder2"
{"files":["INTERLOK-3542.json","INTERLOK-3543.json","INTERLOK-3545.json","INTERLOK-3546.json","INTERLOK-3547.json"]}
```


## zip-service api

This workflow API allows you to zip (compress) a directory.

![workflow diagram](/zip-api.png "zip-service api workflow diagram")

The actual move-file-service has the following config
```xml
<zip-service>
    <directory-path>${test-base-directory-path}\%message{folder}</directory-path>
</zip-service>
```

the api uri:
* /zip

the parameters:
* folder //the folder within ${test-base-directory-path} that we want to zip


```
$ curl -s "http://localhost:8080/zip?folder=test-folder" > src/test/resources/test.zip

$ ls src/test/resources/test.zip
src/test/resources/test.zip
```

## unzip-service api

This workflow API allows you to specify a zip file, and a destination, and it will unzip the file to the given destination

![workflow diagram](/unzip-api.png "unzip-service api workflow diagram")

the bulk of the workflow is a read-file-service, a unzip-service and a move-file-service, using a combination of variables and request parameters.
```xml
<!-- read the zip file into payload -->
<read-file-service>
    <file-path>${test-base-directory-path}\%message{file}</file-path>
</read-file-service>

<!-- unzips the payload to a temp directory and puts the unzip path into the payload -->
<unzip-service/> 

<!-- move the temp unzipped directory to the supplied dest -->
<move-file-service>
    <original-path>%message{%payload}</original-path>
    <new-path>${test-base-directory-path}\%message{dest}</new-path>
    <move-directory>true</move-directory>
</move-file-service>
```    

the api uri:
* /unzip

the parameters:
* file //the zipped file within ${test-base-directory-path} that we want to unzip
* dest //the folder within ${test-base-directory-path} where we want our unzipped contents

```
$ ls src/test/resources
test.zip

$ curl -s "http://localhost:8080/unzip?file=test.zip&dest=test"
unzip success

$ ls src/test/resources
test/  test.zip

$ ls src/test/resources/test
test-folder/

$ ls src/test/resources/test/test-folder/
INTERLOK-3542.json INTERLOK-3543.json INTERLOK-3545.json INTERLOK-3546.json INTERLOK-3547.json
```

## delete-directory-service api

This workflow API allows you to specify a directory to delete

![workflow diagram](/delete-api.png "delete-directory-service api workflow diagram")

The actual delete-directory-service has the following config
```xml
<delete-directory-service>
    <path class="constant-data-input-parameter">
        <value>${test-base-directory-path}/%message{folder}</value>
    </path>
</delete-directory-service>
```

the api uri:
* /delete

the parameters:
* folder //the folder within ${test-base-directory-path} that we want to remove

```
$ ls src/test/resources/
messages/  test/  test.zip  test-folder/

$ ls src/test/resources/test
test-folder/

$ ls src/test/resources/test/test-folder/
INTERLOK-3542.json*  INTERLOK-3543.json*  INTERLOK-3545.json*  INTERLOK-3546.json*  INTERLOK-3547.json*

$ curl -s "http://localhost:8080/delete?folder=test/test-folder"
delete success

$ curl -s "http://localhost:8080/delete?folder=test"
delete success

$ ls src/test/resources/
messages/  test.zip  test-folder/
```
