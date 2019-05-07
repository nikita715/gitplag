FROM node:8 as react-build

WORKDIR /app/frontend

COPY / /app/frontend

RUN yarn \
    && rm -rf ./node_modules/babel-jest

ARG REACT_APP_BACKEND_URL
ENV REACT_APP_BACKEND_URL=$REACT_APP_BACKEND_URL

RUN echo "REACT_APP_BACKEND_URL=$REACT_APP_BACKEND_URL" > .env

RUN yarn build

FROM nginx:1.12-alpine as prod

COPY --from=react-build /app/frontend/build /usr/share/nginx/html

CMD ["nginx", "-g", "daemon off;"]