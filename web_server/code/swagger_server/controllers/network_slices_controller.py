import connexion
import psycopg2
import json

from swagger_server.models.slice import Slice  # noqa: E501
from swagger_server.models.slice_create import SliceCreate  # noqa: E501
from swagger_server import util
from flask import Response, jsonify


def get_db_connection():
    conn = psycopg2.connect(host='172.17.0.2', #change this to the ip of the db - 192.168.221.128
                            database='db',
                            user='postgres', #this ins't secure
                            password='postgres') #this ins't secure
    return conn

def add_slice(body):  # noqa: E501
    """Add an array of network slices to the infrastructure

    Add an array of network slices to the infrastructure # noqa: E501

    :param body: Create a new network slice in the infrastructure
    :type body: dict | bytes

    :rtype: Slice
    """
    if connexion.request.is_json:        
        try:
            body = [SliceCreate.from_dict(d) for d in connexion.request.get_json()] # noqa: E501
        except Exception as e:
            response = jsonify({"error": str(e)})
            response.status_code = 400
            return response
        
        end = []
        
        for slice_data in body:
            try:
                conn = get_db_connection()
                cur = conn.cursor()
                cur.execute("INSERT INTO slice (use_case, sst_type, dw_bw, up_bw, latency, loss_prob, jitter, dscp, _5qi, active) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s) RETURNING slice_id;", (slice_data.use_case_type, slice_data.service_slice_type_sst, slice_data.downlink_user_throughput_bps_ue, slice_data.uplink_user_throughput_bps_ue, slice_data.e2e_latency_ms, slice_data.loss_probability, slice_data.jitter, slice_data.dscp_levels, slice_data._5qi_levels,slice_data.active))
                slice_id = cur.fetchone()[0]
                conn.commit()
                cur.close()
                conn.close()
            except Exception as e:
                response = jsonify({"error": str(e)})
                response.status_code = 500
                return response
        
            body_dict = slice_data.to_dict()
            body_dict['id'] = slice_id
            end.append(body_dict)
            
        response = Response(json.dumps(end), content_type='application/json')    
        
        return response
    
    response = jsonify({"error": "Invalid input"})
    response.status_code = 400
    return response


def delete_slice(slice_id):  # noqa: E501
    """Deletes a network slice

    Delete a network slice # noqa: E501

    :param slice_id: Network slice id to delete
    :type slice_id: int

    :rtype: None
    """
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        cur.execute("DELETE FROM slice WHERE slice_id = %s;", str(slice_id))
        conn.commit()
        cur.close()
        conn.close()
    except Exception as e:
        response = jsonify({"error": str(e)})
        response.status_code = 500
      
    response = jsonify()
    response.status_code = 200  
    return response


def get_slice_by_id(slice_id):  # noqa: E501
    """Find a network slice by ID

    Returns a single network slice # noqa: E501

    :param slice_id: ID of network slice to return
    :type slice_id: int

    :rtype: Slice
    """
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        cur.execute("SELECT * FROM slice WHERE slice_id = %s;", str(slice_id))
        slice_data = cur.fetchone()
        cur.close()
        conn.close() 
        
        if slice_data is None:
            response = jsonify({"error": "Slice not found"})
            response.status_code = 404
            return response


        slice = Slice(*slice_data)

        # Convert list of Slice objects to list of dictionaries
        slice_dict = slice.to_dict()

        # Convert list of dictionaries to JSON
        slice_json = json.dumps(slice_dict)

        response = Response(slice_json, content_type='application/json')

    except Exception as e:
        response = jsonify({"error": str(e)})
        response.status_code = 500

    return response


def slice_get():  # noqa: E501
    """Gets the existing slices in the infrastructure

    Gets the existing slices in the infrastructure # noqa: E501


    :rtype: Slice
    """
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        cur.execute("SELECT * FROM slice;")
        slices = cur.fetchall()
        cur.close()
        conn.close() 

        # Convert fetched data to list of Slice objects
        slices_list = [Slice(*slice) for slice in slices]

        # Convert list of Slice objects to list of dictionaries
        slices_dict_list = [slice.to_dict() for slice in slices_list]

        # Convert list of dictionaries to JSON
        slices_json = json.dumps(slices_dict_list)

        response = Response(slices_json, content_type='application/json')

    except Exception as e:
        response = jsonify({"error": str(e)})
        response.status_code = 500

    return response


def update_slice(slice_id, body=None):  # noqa: E501
    """Updates a network slice

    Updates a network slice # noqa: E501

    :param slice_id: Network slice id to update
    :type slice_id: int
    :param body: Update an existent network slice in the infrastructure
    :type body: dict | bytes

    :rtype: Slice
    """
    if connexion.request.is_json:
        try:
            body = SliceCreate.from_dict(connexion.request.get_json()) # noqa: E501
        except Exception as e:
            response = jsonify({"error": str(e)})
            response.status_code = 400
            return response
        
        try:
            conn = get_db_connection()
            cur = conn.cursor()
            cur.execute("UPDATE slice SET use_case = %s , sst_type = %s, dw_bw = %s, up_bw = %s, latency = %s, loss_prob = %s, jitter = %s, dscp = %s, _5qi = %s, active = %s WHERE slice_id = %s RETURNING slice_id;", (body.use_case_type, body.service_slice_type_sst, body.downlink_user_throughput_bps_ue, body.uplink_user_throughput_bps_ue, body.e2e_latency_ms, body.loss_probability, body.jitter, body.dscp_levels, body._5qi_levels, body.active, slice_id))
            slice_id = cur.fetchone()[0]
            conn.commit()
            cur.close()
            conn.close()
        except Exception as e:
            if str(e) == '\'NoneType\' object is not subscriptable':
                response = jsonify({"error": "Slice not found"})
                response.status_code = 404
                return response
            
            response = jsonify({"error": str(e)})
            response.status_code = 500
            return response
        
        body_dict = body.to_dict()
        body_dict['id'] = slice_id
        response = Response(json.dumps(body_dict), content_type='application/json')    
        
        return response
        
    response = jsonify({"error": "Invalid input"})
    response.status_code = 400
    return response



