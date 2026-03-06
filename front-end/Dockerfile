FROM node:20

WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .

EXPOSE 5173

# Lance Vite sur toutes les interfaces
CMD ["npm", "run", "dev", "--", "--host"]
