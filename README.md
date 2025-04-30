Test Application
----------------

Run program with `-f src/test/resources/test.yaml output`

Start webserver:
```bash
docker compose up
```

See XML-Feed by visiting http://localhost:8080/feed/test.xml

Use a Feed Reader plugin in your Browser and add the feed http://localhost:8080/feed/test.xml

Test the Feed by using the INSPIRE Atom Client Plugin in QGIS by providing the URL http://localhost:8080/feed/test.xml
