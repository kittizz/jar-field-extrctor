# JAR Field Extractor

A simple utility to extract field values from classes in JAR files using reflection.

## Description

JAR Field Extractor is a Kotlin-based command-line tool that allows you to extract the value of a static field from a class within a JAR file without needing to include the JAR in your classpath or writing custom code.

This tool is particularly useful for:
- Extracting version information from software
- Checking configuration constants
- Debugging or inspecting JAR contents
- Automated testing or version verification

## Installation

1. Clone this repository:
   ```bash
   git clone https://github.com/kittizz/jar-field-extrctor.git
   ```

2. Build the project using Gradle:
   ```bash
   ./gradlew shadowJar
   ```

The executable JAR will be created in the `build/libs` directory.

## Usage

```bash
java -jar jar-field-extractor.jar <jar_file> <class_name> <field_name>
```

### Parameters

- `jar_file`: Path to the JAR file you want to inspect
- `class_name`: Fully qualified name of the class containing the field
- `field_name`: Name of the static field you want to extract

### Example

```bash
java -jar jar-field-extractor.jar nukkit.jar cn.nukkit.network.protocol.ProtocolInfo MINECRAFT_VERSION_NETWORK
```

This command will extract the value of the `MINECRAFT_VERSION_NETWORK` constant from the `ProtocolInfo` class in the Nukkit JAR file.

## Requirements

- Java 23 or higher
- Kotlin runtime (included in the shadow JAR)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
