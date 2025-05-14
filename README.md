Running Application
-------------------

Build and run the application with the command:

```bash
mvn clean package && docker compose up
```

This will run the atom-creator once and start a webserver to serve the feeds. Additionally,
a scheduler is built in to run the atom-creator once every night.

If you want to run the atom-creator in your IDE, create an IntelliJ run configuration for the
`AtomCreator` class with cli arguments `--clean -f docker/test.yaml docker/data http://localhost:8080`.
It might be necessary to delete the files in the cache directory manually beforehand:

```shell
sudo find docker/data/cache -mindepth 1 -type d -exec rm -r {} + && sudo rm docker/data/feeds/*.xml
```

If you want to rerun the application inside docker you can use:

```bash
mvn clean package && docker compose run --build --remove-orphans atom-creator
```

Test Application
----------------

See XML-Feed by visiting http://localhost:8080/feeds/test.xml

Use a Feed Reader plugin in your Browser and add the feed http://localhost:8080/feeds/test.xml

Test the Feed by using the INSPIRE Atom Client Plugin in QGIS by providing the URL http://localhost:8080/feeds/test.xml
