mkdir -p executables
cd server/
mvn clean install package
mv target/server18.war ../../executables
cd ../../client/
mvn clean install package
mv client18.jar ../../executables