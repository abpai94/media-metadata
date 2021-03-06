# Media Metadata
Coding task to create a REST API to handle Media JSON data for Movies and TV Series.

## REST API operations

### Add movie data
    POST /movie

    {
    "id": "value",
    "title": "value",
    "labels": [
    "value"
    ],
    "director": "value",
    "releaseDate": value
    }

### Add series data
    POST /series

    {
    "id": "value",
    "title": "value",
    "labels": [
    "value"
    ],
    "numberOfEpisodes": value
    }

### Find all media data
    GET /media

### Find all movies
    GET /media/movies

### Find all series
    GET /media/series

### Find all related media by label genre
    GET /media/related/{id}

### Find all media data based on UUID
    GET /media/{id}

### Find media using character match of title
    GET /media?title={value}

### Delete media using UUID
    DELETE /media/{id}

## Previous commits in [abpai94/coding-tests](https://github.com/abpai94/coding-tests)
* [Commit 2dbeeb3](https://github.com/abpai94/coding-tests/commit/241a2cc9f1559ccd6ea19c4f97c9e4ff694c528) Started working on a new company technical challenge to create a REST API for VoD metadata which can use CRUD queries.
* [Commit e540603](https://github.com/abpai94/coding-tests/commit/e5406038f641851a8f2dd76f7911b45b004aa9eb) Able to find movies based on regex strings.
* [Commit e540603](https://github.com/abpai94/coding-tests/commit/e5406038f641851a8f2dd76f7911b45b004aa9eb) Implemented Deserialising methods, created MediaType enums assisting in retrieving Movies and Series JSON objects.
* [Commit ebdbde5](https://github.com/abpai94/coding-tests/commit/ebdbde5429a17269290dfe20a9022e7b29903863) Added delete endpoint and retrieving deleted movies/series endpoint.
* [Commit e22d427](https://github.com/abpai94/coding-tests/commit/0952a9925d85211289889a822893fc456cc96951) Able to find media based on labels.
* [Commit 0952a99](https://github.com/abpai94/coding-tests/commit/766e53b1329220719f73de603fa9ae35458c188f) Fixed issue with deleting values in label hashmap.
* [Commit 766e53b](https://github.com/abpai94/coding-tests/commit/fc436d22d8ae11635f45052f91c95db81de42b0c) Removed overly complicated methods to add and remvoe media to label based data structure, removed unused getters and setters.
* [Commit fc436d2](https://github.com/abpai94/coding-tests/commit/34dec1c660a54d339d1999fac63491c06bd8626a) Added comments to all classes.
