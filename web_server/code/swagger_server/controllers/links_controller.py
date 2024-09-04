import connexion
import psycopg2
import json

from swagger_server.models.link import Link  # noqa: E501
from flask import Response, jsonify

def get_db_connection():
    conn = psycopg2.connect(host='172.17.0.2', #change this to the ip of the db - 192.168.221.128
                            database='db',
                            user='postgres', #this ins't secure
                            password='postgres') #this ins't secure
    return conn

def add_links_info(body=None):  # noqa: E501
    """Add an array of link information

    Add an array of link information # noqa: E501

    :param body: Associate link metrics to a network link
    :type body: list | bytes

    :rtype: None
    """
    if connexion.request.is_json:
        try:
            body = [Link.from_dict(d) for d in connexion.request.get_json()]  # noqa: E501
        except Exception as e:
            response = jsonify({"error": str(e)})
            response.status_code = 400
            return response
        
        #end = []
        
        for link in body:
            try:
                conn = get_db_connection()
                cur = conn.cursor()
                
                cur.execute("UPDATE link SET bw = %s, latency = %s, jitter = %s, loss_prob = %s, energy_consumption = %s WHERE port_number_src = %s AND port_number_dst = %s AND port_device_uri_src = %s AND port_device_uri_dst = %s AND port_device_timestamp_sim_dst = (SELECT MAX(port_device_timestamp_sim_dst) FROM link) RETURNING *;", (link.bandwith, link.latency, link.jitter, link.loss_prob, link.energy_consumption, link.port_numb_src, link.port_numb_dst, link.sw_id_src, link.sw_id_dst))

                help = cur.fetchall()

                if len(help) == 0:
                    response = jsonify({"error": "Invalid input - link not found in the database"})
                    response.status_code = 400
                    return response
                    
                conn.commit()
                cur.close()
                conn.close()
            except Exception as e:
                response = jsonify({"error": str(e)})
                response.status_code = 500
                return response
        
        response = jsonify({"success": 200})   
        response.status_code = 200
        return response
    
    response = jsonify({"error": "Invalid input"})
    response.status_code = 400
    return response


def links_get():  # noqa: E501
    """Gets the existing link information

    Gets the existing link information # noqa: E501


    :rtype: List[Link]
    """
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        cur.execute("SELECT * FROM link WHERE port_device_timestamp_sim_dst = (SELECT MAX(port_device_timestamp_sim_dst) FROM link);")
        links = cur.fetchall()
        cur.close()
        conn.close()
        
        #Convert fetched data to list of Link objects
        link_list = []
        for link in links:
            sw_id_src = link[11]
            sw_id_dst = link[12]
            port_numb_src = link[7]
            port_numb_dst = link[8]
            bandwith = link[2]
            latency = link[3]
            jitter = link[4]
            loss_prob = link[5]
            energy_consumption = link[6]
            
            link_add = Link(sw_id_src=sw_id_src, sw_id_dst=sw_id_dst, port_numb_src=port_numb_src, port_numb_dst=port_numb_dst, bandwith=bandwith, latency=latency, jitter=jitter, loss_prob=loss_prob, energy_consumption=energy_consumption)
            link_list.append(link_add)
            
        #Convert the list of Link objects to a list of dictionaries
        link_dict_list = [link.to_dict() for link in link_list]
        
        #Convert list of dictionaries to json
        links_json = json.dumps(link_dict_list)
        
        response = Response(links_json, content_type='application/json')
        
    except Exception as e:
        response = jsonify({"error": str(e)})
        response.status_code = 500
    
    return response