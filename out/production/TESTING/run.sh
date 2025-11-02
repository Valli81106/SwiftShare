#!/bin/bash

# Compile the Java application
echo "Compiling SwiftShare..."
mkdir -p bin
javac -d bin -sourcepath src src/com/swiftshare/gui/frames/MainFrame.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Starting SwiftShare GUI..."
    java -cp bin com.swiftshare.gui.frames.MainFrame
else
    echo "Compilation failed!"
    exit 1
fi
