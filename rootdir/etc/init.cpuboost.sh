# cpuboost initialization script by ronax
# If there is not a persist value, we need to set one
if [ ! -f /data/property/persist.cpuboost.profile ]; then
    setprop persist.cpuboost.profile 0
fi

