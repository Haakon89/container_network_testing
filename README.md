# container_network_testing
testing builds of a network simulation program I am working on
How to run:
    Go to Main class and click run.

📂 File Menu
    📄 New Project – Name your project & choose a working folder.
    💾 Save Project – Save to the current project folder.
    📂 Open Project – Load an existing project from a folder.

🌐 Add Network
    🔹 Standard Network – Quick default setup.
    🛠 Custom Network – Choose name & network size.

💻 Add Device (goes to Unassigned Devices)
    🛠 Custom Device – (Work in Progress) Create a custom device
    🖥 Standard – Basic Ubuntu device.
    🌍 DNS – Domain Name System server.
    📜 Web – Web server with a sample page.
    🖨 Printer – (Work in Progress) Network printer simulation.

⚙ Build
    🏗 Generate all Dockerfiles & docker-compose.yml for your project.

▶ Run
    🚀 Build & start the Docker environment.
    🎯 Enter an entrypoint (if set) or
    ⏹ Shutdown the environment.

🖱 Device Menu (Right-click on a device)
    🔀 Assign Device – Move to a network/group.
    ❌ Delete Device – Remove from project.
    ✏ Edit Device – Update device details.

🖱 Network Menu (Right-click on a network)
    ➕ Assign Device – Add a device here.
    🔄 Move Devices – Transfer all from another network/group.
    ❌ Delete Network – Remove network & its devices.
    ✏ Edit Network – Rename or resize network.

Prerequisites

This application requires Docker to be installed separately (currently only works with linux):

    Linux: Docker Engine
    - Set Docker to run without sudo cmd or it will fail

    Windows/macOS: Docker Desktop

You must have a Docker plan appropriate for your organization and usage:

    Free to use for:

        Personal projects

        Educational use

        Non-commercial open-source projects

        Small businesses with fewer than 250 employees AND under US $10 million in annual revenue

    Paid subscription required for:

        Larger businesses

        Government use

        Any use that exceeds the above limits

Check the Docker Subscription Service Agreement for the most up-to-date terms.


How This Application Uses Docker

This tool:

    Generates Dockerfile and docker-compose.yml files based on your configuration.

    Executes Docker commands via your local terminal/CLI.

    Does not include or redistribute Docker.

    Does not provide a Docker license—you must accept Docker’s license terms yourself.

Important Notes

    Trademarks: “Docker” and the Docker logo are trademarks of Docker, Inc. Use of the word “Docker” in this project is for descriptive purposes only and does not imply endorsement.

    Docker Hub limits: Free accounts have pull rate limits. For heavy use, log in to Docker Hub or configure a private registry.