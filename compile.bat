@echo off
echo Compiling SwiftShare

if not exist bin mkdir bin

javac -d bin src\com\swiftshare\models\*.java
javac -d bin -cp bin src\com\swiftshare\network\core\*.java
javac -d bin -cp bin src\com\swiftshare\network\transfer\*.java
javac -d bin -cp bin src\com\swiftshare\network\discovery\*.java
javac -d bin -cp bin src\com\swiftshare\network\utils\*.java
javac -d bin -cp bin src\com\swiftshare\network\manager\*.java

javac -d bin -cp bin test\com\swiftshare\network\*.java

echo Done!
echo.
echo Run tests:
echo   java -cp bin com.swiftshare.network.NetworkTest
echo   java -cp bin com.swiftshare.network.IntegrationTest
echo   java -cp bin com.swiftshare.network.SimpleServer
echo   java -cp bin com.swiftshare.network.SimpleClient