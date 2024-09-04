# coding: utf-8

from __future__ import absolute_import

from flask import json
from six import BytesIO

from swagger_server.models.slice import Slice  # noqa: E501
from swagger_server.models.slice_create import SliceCreate  # noqa: E501
from swagger_server.test import BaseTestCase


class TestNetworkSlicesController(BaseTestCase):
    """NetworkSlicesController integration test stubs"""

    def test_add_slice(self):
        """Test case for add_slice

        Add an array of network slices to the infrastructure
        """
        body = [SliceCreate()]
        response = self.client.open(
            '/slice',
            method='POST',
            data=json.dumps(body),
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_delete_slice(self):
        """Test case for delete_slice

        Deletes a network slice
        """
        response = self.client.open(
            '/slice/{sliceId}'.format(slice_id=789),
            method='DELETE')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_get_slice_by_id(self):
        """Test case for get_slice_by_id

        Find a network slice by ID
        """
        response = self.client.open(
            '/slice/{sliceId}'.format(slice_id=789),
            method='GET')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_slice_get(self):
        """Test case for slice_get

        Gets the existing network slices in the infrastructure
        """
        response = self.client.open(
            '/slice',
            method='GET')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_update_slice(self):
        """Test case for update_slice

        Updates a network slice
        """
        body = SliceCreate()
        response = self.client.open(
            '/slice/{sliceId}'.format(slice_id=789),
            method='PUT',
            data=json.dumps(body),
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
