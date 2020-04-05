local-kafka: export SWARM_HOST = localhost
local-kafka: docker stack deploy --compose-file kafka.stack.yml kafka