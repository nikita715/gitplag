FROM node:8 as react-build
RUN git clone https://github.com/nikita715/gitplag.git /app --branch dev
WORKDIR /app/frontend
RUN yarn
RUN rm -rf ./node_modules/babel-jest
RUN yarn build
CMD yarn run start
