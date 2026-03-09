#!/bin/bash
# Script to open terminals for back-end and front-end

# Get the directory where the script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Open terminal in back-end directory and start Spring Boot
x-terminal-emulator --working-directory="$SCRIPT_DIR/back-end" -e "bash -c 'echo \"=== Lancement de Spring Boot ===\"; cd \"$SCRIPT_DIR/back-end\" && ./mvnw spring-boot:run; bash'" &

# Open terminal in front-end directory and run npm run dev
x-terminal-emulator --working-directory="$SCRIPT_DIR/front-end" -e "bash -c 'echo \"=== Lancement du serveur Vue.js (npm run dev) ===\"; cd \"$SCRIPT_DIR/front-end\" && npm run dev; bash'" &

exit 0

