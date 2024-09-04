# coding: utf-8

from __future__ import absolute_import

from flask import json
from six import BytesIO

from swagger_server.models.flowrule import Flowrule  # noqa: E501
from swagger_server.test import BaseTestCase


class TestFlowRulesController(BaseTestCase):
    """FlowRulesController integration test stubs"""

    def test_flowrules_get(self):
        """Test case for flowrules_get

        Gets the existing flow rules
        """
        response = self.client.open(
            '/flowrules',
            method='GET')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
