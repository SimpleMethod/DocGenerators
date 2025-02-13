# Document Generator Plugin System

A flexible and extensible plugin-based system for generating and manipulating Word documents. This system allows for dynamic document generation with support for text formatting, image insertion, and markdown processing.

## 🌟 Features

- 📝 Plugin-based architecture for easy extension
- 🎨 Rich text formatting with Markdown support
- 🖼️ Image insertion capabilities
- 📋 Template-based document generation
- 🔄 Dynamic content replacement
- 📱 Service operation registry for plug-and-play functionality

## 🛠️ Core Components

### Plugin System
- Dynamic plugin loading and unloading
- Plugin lifecycle management (DISCOVERED, INITIALIZED, ENABLED, DISABLED, ERROR)
- Operation registry for service discovery

### Document Processing
- Text search and replacement
- Markdown processing
- Image insertion (header, footer, inline)
- Table manipulation
- Style management

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Spring Boot 3.x
- Maven 3.x

### Installation

1. Clone the repository:
```bash
git clone https://github.com/SimpleMethod/DocGenerators.git
```

2. Build the project:
```bash
mvn clean install
```

3. Create plugins directory:
```bash
mkdir plugins
```

### Creating a Plugin

1. Create a new Maven project
2. Add the core API dependency:
```xml
<dependency>
    <groupId>pl.mlodawski</groupId>
    <artifactId>document-generator-api</artifactId>
    <version>1.0.0</version>
</dependency>
```

3. Implement the plugin:
```java
@Slf4j
public class CustomPlugin extends AbstractPlugin {
    private static final String ID = "custom-plugin";
    private static final String NAME = "Custom Plugin";
    private static final String VERSION = "1.0.0";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }
}
```

## 📚 Example Usage

### Template Processing

```java
// Initialize plugin
pluginManager.initializePlugin("example-plugin");
pluginManager.enablePlugin("example-plugin");

// Set template and image
Map<String, Object> params = new HashMap<>();
params.put("templateDocument", templateBytes);
pluginManager.invokePluginMethod("example-plugin", "setTemplateDocument", params);

params.clear();
params.put("exampleImage", imageBytes);
pluginManager.invokePluginMethod("example-plugin", "setExampleImage", params);

// Process template
byte[] result = (byte[]) pluginManager.invokePluginMethod(
    "example-plugin", 
    "processTemplate", 
    new HashMap<>()
);
```

### Markdown Support

The system supports rich text formatting using Markdown syntax:
```java
String markdownText = "~~Document generator~~ generate your *files* in this system, " +
                     "**which means all your files** are [automatically](https://mlodaw.ski) " +
                     "saved locally and are accessible **offline!**";

context.executeOperation(
    "MarkdownTextService.processMarkdown",
    paragraph,
    markdownText
);
```

## 🔌 Available Services

- **SearchTextService**: Text search and replacement in documents
- **InsertImagesService**: Image insertion into different document sections
- **MarkdownTextService**: Markdown processing and formatting
- **ParagraphBuilderService**: Paragraph style and formatting
- **ManageTableService**: Table manipulation and formatting

## 🏗️ Architecture

```
document-generator/
├── api/                    # Core API interfaces
├── core/                   # Core implementation
├── plugins/               # Plugin directory
│   └── example-plugin/    # Example plugin implementation
└── services/              # Core services
    ├── document/          # Document processing
    ├── markdown/          # Markdown processing
    └── image/             # Image processing
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the GNU Affero General Public License v3.0 - see the [LICENSE](https://github.com/SimpleMethod/DocGenerators/blob/master/LICENSE) file for details.

## 📬 Contact

Michał Młodawski - michal@mlodaw.ski

Project Link: [https://github.com/SimpleMethod/DocGenerators](https://github.com/SimpleMethod/DocGenerators)
