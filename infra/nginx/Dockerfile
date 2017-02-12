FROM nginx

RUN mkdir /etc/nginx/certs

COPY dot-ssl/localhost.crt /etc/nginx/certs/ca.crt
COPY dot-ssl/localhost.key /etc/nginx/certs/ca.key

RUN rm /etc/nginx/conf.d/default.conf
COPY default.conf /etc/nginx/conf.d/default.conf

EXPOSE 80 443

CMD ["nginx", "-g", "daemon off;"]

