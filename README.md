# KeepTask

<img src="src/main/resources/icons/icon.png" align="left" style="padding-right:10px" width="50px"/>
KeepTask is a Java application built with JavaFX that provides a convenient way to track your tasks and manage your to-do lists. This README file serves as a guide to help you understand the application and get started with it.

The Ui looks like the following:
![Main Ui](readme/keeptask.png?raw=true)  
 
## Features
* **Quickly add TODOs**: The app is designed to allow you to quickly add new tasks to your to-do list.
* **Markdown-style links**: You can use markdown-style links within your TODOs, making it easy to reference external resources or related tasks.
* **Persistent storage**: The app uses an H2 database to persist your TODOs, ensuring that your tasks are saved even if you close the application.
* **Flexible filtering**: The app provides a filter feature that allows you to easily sort and view specific subsets of your tasks. You can filter tasks based on their status (e.g., completed, pending) or other criteria to focus on relevant items.
* **All on your local machine**: KeepTask ensures that all your data remains on your local machine. There is no sharing of data with third parties, preserving your privacy and security.
* **Open-source**: The app is open-source, allowing you to view, modify, and enhance the codebase. You are encouraged to fork the repository and add your own features to customize the application according to your needs.

## Installation
To run KeepTask, follow these steps:

1. Make sure you have Java 11 installed on your system.
1. Download the .jar file to your local machine. See [Releases](https://github.com/Death111/KeepTask/releases) for the latest version.
1. Run the application, and you should see the main interface of KeepTask.

## Usage
KeepTask is still in development, but you can already start using it to manage your tasks. Here's a brief overview of its usage:

1. Launch the application, and you will be presented with the main interface.
1. To add a new TODO, click on the "Add" button or use a shortcut key if available.
1. Enter the details of your task in the input field, and you can use markdown-style links by enclosing the link text in square brackets followed by the URL in parentheses. For example: `Visit [Google](https://www.google.com)`.
1. Click the "Save" button to add the TODO to your list.
1. Your newly added task will appear in the TODO list.
1. To mark a task as completed, click the checkbox next to it.
1. You can edit or delete existing TODOs by right-clicking on the task and selecting the appropriate option from the context menu.

## Contributing
Contributions to the TODO-Tracking App are welcome! If you'd like to contribute, please follow these steps:

Fork the repository.
Create a new branch for your feature or bug fix.
Implement your changes.
Write tests to ensure the stability of the application (if applicable).
Commit your changes and push them to your fork.
Submit a pull request, describing your changes in detail and providing any necessary information or context.
Please note that this project is still in development, so it's a good idea to check the existing issues or discuss with the project maintainers before starting significant work.

## License
KeepTask is licensed under the GNU General Public License. Feel free to use, modify, and distribute the application as per the terms of the license.
KeepTask is based on [KeepTime](https://github.com/doubleSlashde/KeepTime), which is licensed under the GPL (GNU General Public License) version 3.0. We are grateful for the foundational work provided by the original repository, which served as a starting point for our application.

To contribute to KeepTask, it is essential to acknowledge and respect the license terms of the base repository. When submitting a pull request or making modifications, ensure that your changes comply with the GPL license requirements. Refer to the original repository's license and guidelines for more information on contributing to the base project.