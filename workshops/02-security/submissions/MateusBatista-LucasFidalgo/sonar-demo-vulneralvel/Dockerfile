# Dockerfile da aplicação Node vulnerável
FROM node:20-alpine

# Diretório de trabalho dentro do container
WORKDIR /usr/src/app

# Copia apenas arquivos de dependência primeiro (melhora cache)
COPY package*.json ./

# Instala dependências (sem dev, pra imagem ficar menor)
RUN npm install --omit=dev

# Copia o código fonte
COPY src ./src

# Porta que o app usa
EXPOSE 3000

# Comando de inicialização
CMD ["npm", "start"]
