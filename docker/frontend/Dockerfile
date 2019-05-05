# Stage 1
FROM node:8 as react-build
RUN git clone https://github.com/nikita715/gitplag.git /app --branch dev
WORKDIR /app/frontend
RUN yarn
CMD yarn run start