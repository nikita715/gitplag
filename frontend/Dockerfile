FROM node:8 as react-build

WORKDIR /app/frontend

COPY / /app/frontend

RUN npm install \
    && rm -rf ./node_modules/babel-jest

RUN npm run build

FROM nginx:1.12-alpine as prod

COPY --from=react-build /app/frontend/build /usr/share/nginx/html

RUN rm /etc/nginx/conf.d/default.conf
COPY --from=react-build /app/frontend/nginx.conf /etc/nginx/conf.d/default.conf

CMD ["nginx", "-g", "daemon off;"]