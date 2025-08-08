# VisionMate Chat Assistant

This project demonstrates a GUI-based chat application built with LangChain4j, utilizing Ollama for language model interactions and integrating with Google Calendar for event management. It also supports sending emails and image attachments.

## Overview

VisionMate Chat Assistant provides a graphical user interface for interacting with a language model (served by Ollama) and managing Google Calendar events. You can send text and images to the language model, create, list, and delete calendar events, and send emails.

## Prerequisites

*   Java Development Kit (JDK) 17 or higher
*   Maven
*   Ollama
*   Google Cloud project with the Google Calendar API enabled
*   Microsoft 365 account for sending emails

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

    Download and install the Windows preview from the [official website](https://ollama.com/). Note that the Windows version is still in preview and may have limitations.

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

LangChain4j is included as a dependency in this project's `pom.xml` file. Maven will automatically download and manage the LangChain4j library and its dependencies when you build the project. No manual installation is required.

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

    Choose any model from [Ollama's library](https://ollama.com/library). Ensure the `modelName` in `LangChainBackend.java` matches the model you pull.

3.  **Configure Ollama endpoint:**

    The default endpoint is `http://localhost:11434`. If you are running Ollama on a different host or port, modify the `ollama.base-url` property in the `LangChainBackend.java` file.

4.  **Configure Google Calendar API:**

    *   Enable the Google Calendar API in your Google Cloud project.
    *   Create a service account and download the credentials file (`credentials.json`).
    *   **Important:** Place the `credentials.json` file in the `src/main/java/com/example/langchain4j/` directory. **Do not commit this file to your repository!** Use environment variables or a more secure method for production.
    *   The application uses the `tokens` directory to store authorization tokens. This directory is created automatically when you run the application for the first time.

5.  **Configure Email Settings:**

    The `EmailTool.java` file contains hardcoded email credentials. **This is highly discouraged.** You should use environment variables or a more secure method for storing and accessing these credentials. The current settings are:

    *   `from`: `"<YOUR EMAIL>"`
    *   `password`: `"<YOUR PASSWORD>"`
    *   `smtp.host`: `"smtp.office365.com"`
    *   `smtp.port`: `"587"`

    **Replace these values with your own Microsoft 365 account credentials.**

6.  **Build the project:**

    ```bash
    mvn clean install
    ```

## Running the Application

Run the App.java File
