# Running a topology script

### 1. Configure the ip of the web server in the topology script
### 2. With the db and the onos running reset and initialize the db
```
onos:database -r
```
```
onos:database -iD
```
### 3. Run the topology script
```
sudo -E python3 topologyScript.py
```
### 4. Before press continue in the mininet terminal, run the snapshot command in the onos cli
```
onos:database -s
```
### 5. Press continue in the mininet terminal
### 6. Verify that the API command retuned successfully
### 7. Have fun messing arround!
