# coding: utf-8

from __future__ import absolute_import

from flask import json
from six import BytesIO

from swagger_server.models.link import Link  # noqa: E501
from swagger_server.test import BaseTestCase


class TestLinksController(BaseTestCase):
    """LinksController integration test stubs"""

    def test_add_links_info(self):
        """Test case for add_links_info

        Add an array of link information
        """
        body = [Link()]
        response = self.client.open(
            '/links',
            method='POST',
            data=json.dumps(body),
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_links_get(self):
        """Test case for links_get

        Gets the existing link information
        """
        response = self.client.open(
            '/links',
            method='GET')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
