docker rm vl-mart
docker rmi vlvcoder/vl-mart

docker build --no-cache --tag=vlvcoder/vl-mart:latest .

docker run --name vl-mart -it -p 8081:8080 vlvcoder/vl-mart

