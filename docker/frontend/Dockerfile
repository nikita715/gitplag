FROM node:8 as react-build
RUN git clone https://github.com/nikita715/gitplag.git /app --branch dev
WORKDIR /app/frontend
RUN yarn
RUN rm -rf ./node_modules/babel-jest
RUN yarn build
CMD yarn run start

FROM nginx:1.12-alpine as prod
COPY --from=react-build /app/frontend/build /usr/share/nginx/html
CMD ["nginx", "-g", "daemon off;"]