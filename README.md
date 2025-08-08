# VisionMate

This project demonstrates the use of LangChain4j, a Java library for building language model applications, using Ollama as the language model provider.

## Overview

This demo showcases a simple application that interacts with a language model (served by Ollama) to [briefly describe the application's purpose, e.g., answer questions, generate text, etc.].

## Prerequisites

*   Java Development Kit (JDK) 17 or higher
*   Maven
*   Ollama

## Installation

### 1. Install Ollama

Ollama allows you to run open-source language models locally.

*   **macOS:**

    Download and install Ollama from the [official website](https://ollama.com/).

*   **Linux:**

    Run the following command in your terminal:

    ```bash
    curl -fsSL https://ollama.com/install.sh | sh
    ```

*   **Windows (Preview):**

    Download and install the Windows preview from the [official website](https://ollama.com/).  Note that the Windows version is still in preview and may have limitations.

### 2. Install Maven

Maven is a build automation tool used for building Java projects.

*   **Windows:**

    1.  Download the Maven binary from the [Apache Maven website](https://maven.apache.org/download.cgi). Choose the "Binary zip archive" option.
    2.  Extract the downloaded archive to a directory of your choice (e.g., `C:\apache-maven`).
    3.  Set the `JAVA_HOME` environment variable to your JDK installation directory (e.g., `C:\Program Files\Java\jdk-17`).
    4.  Set the `M2_HOME` environment variable to the Maven installation directory (e.g., `C:\apache-maven`).
    5.  Add the `%M2_HOME%\bin` directory to your `PATH` environment variable.
    6.  Open a new command prompt and verify that Maven is installed correctly by running:

        ```bash
        mvn -version
        ```

*   **macOS:**

    You can install Maven using Homebrew:

    ```bash
    brew install maven
    ```

    Verify the installation:

    ```bash
    mvn -version
    ```

*   **Linux:**

    Use your distribution's package manager to install Maven. For example, on Debian/Ubuntu:

    ```bash
    sudo apt update
    sudo apt install maven
    ```

    Verify the installation:

    ```bash
    mvn -version
    ```

### 3. Install LangChain4j (Dependency Management via Maven)

LangChain4j is included as a dependency in this project's `pom.xml` file. Maven will automatically download and manage the LangChain4j library and its dependencies when you build the project.  No manual installation is required.

## Setup

1.  **Clone the repository:**

    ```bash
    git clone <repository_url>
    cd langchain4j-demo
    ```

    Replace `<repository_url>` with the URL of your GitHub repository.

2.  **Pull the desired model with Ollama:**

    ```bash
    ollama pull mistralai/Mistral-7B-Instruct-v0.2
    ```

    Choose any model from [Ollama's library](https://ollama.com/library).

3.  **Configure Ollama endpoint:**

    The default endpoint is `http://localhost:11434`. If you are running Ollama on a different host or port, modify the `ollama.base-url` property in the `application.properties` file.

4.  **Build the project:**

    ```bash
    mvn clean install
    ```

## Running the Application

```bash
mvn spring-boot:run
