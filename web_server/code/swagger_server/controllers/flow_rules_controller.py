import psycopg2
import json

from swagger_server.models.flowrule import Flowrule  # noqa: E501
from swagger_server import util
from flask import Response, jsonify

def get_db_connection():
    conn = psycopg2.connect(host='172.17.0.2', #change this to the ip of the db - 192.168.221.128
                            database='db',
                            user='postgres', #this ins't secure
                            password='postgres') #this ins't secure
    return conn

def flowrules_get():  # noqa: E501
    """Gets the existing flow rules

    Gets the existing flow rules # noqa: E501


    :rtype: List[Flowrule]
    """
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        cur.execute("SELECT * FROM flowrule WHERE device_timestamp_sim = (SELECT MAX(device_timestamp_sim) FROM flowrule);")
        rules = cur.fetchall()
        cur.close()
        conn.close()
        
        rules_list = []
        for rule in rules:
            rule_id = rule[0]
            state = rule[1]
            rule_bytes = rule[2]
            packets = rule[3]
            duration_treatment = rule[4]
            priority = rule[5]
            table_name = rule[6]
            app_id = rule[7]
            group_id = rule[8]
            timeout = rule[9]
            hard_timeout = rule[10]
            permanent = rule[11]
            selector = rule[12]
            treatment = rule[13]
            device_uri = rule[15]
            
            rule_add = Flowrule(id=rule_id, state=state, bytes=rule_bytes, packets=packets, duration_treatment=duration_treatment, priority=priority, table_name=table_name, app_id=app_id, group_id=group_id, timeout=timeout, hard_timeout=hard_timeout, permanent=permanent, selector=selector, treatment=treatment, device_uri=device_uri)
            rules_list.append(rule_add)
        
        rule_dict_list = [rule.to_dict() for rule in rules_list]
        
        rules_json = json.dumps(rule_dict_list)
        
        response = Response(rules_json, content_type='application/json')
    
    except Exception as e:
        response = jsonify({"error": str(e)})
        response.status_code = 500
        return response
    
    return response
