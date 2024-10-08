# coding: utf-8

from __future__ import absolute_import
from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from swagger_server.models.base_model_ import Model
from swagger_server import util


class SliceCreate(Model):
    """NOTE: This class is auto generated by the swagger code generator program.

    Do not edit the class manually.
    """
    def __init__(self, use_case_type: str=None, service_slice_type_sst: str=None, downlink_user_throughput_bps_ue: float=None, uplink_user_throughput_bps_ue: float=None, e2e_latency_ms: float=None, loss_probability: float=None, jitter: float=None, dscp_levels: List[int]=None, _5qi_levels: List[int]=None, active: bool=None):  # noqa: E501
        """SliceCreate - a model defined in Swagger

        :param use_case_type: The use_case_type of this SliceCreate.  # noqa: E501
        :type use_case_type: str
        :param service_slice_type_sst: The service_slice_type_sst of this SliceCreate.  # noqa: E501
        :type service_slice_type_sst: str
        :param downlink_user_throughput_bps_ue: The downlink_user_throughput_bps_ue of this SliceCreate.  # noqa: E501
        :type downlink_user_throughput_bps_ue: float
        :param uplink_user_throughput_bps_ue: The uplink_user_throughput_bps_ue of this SliceCreate.  # noqa: E501
        :type uplink_user_throughput_bps_ue: float
        :param e2e_latency_ms: The e2e_latency_ms of this SliceCreate.  # noqa: E501
        :type e2e_latency_ms: float
        :param loss_probability: The loss_probability of this SliceCreate.  # noqa: E501
        :type loss_probability: float
        :param jitter: The jitter of this SliceCreate.  # noqa: E501
        :type jitter: float
        :param dscp_levels: The dscp_levels of this SliceCreate.  # noqa: E501
        :type dscp_levels: List[int]
        :param _5qi_levels: The _5qi_levels of this SliceCreate.  # noqa: E501
        :type _5qi_levels: List[int]
        :param active: The active of this SliceCreate.  # noqa: E501
        :type active: bool
        """
        self.swagger_types = {
            'use_case_type': str,
            'service_slice_type_sst': str,
            'downlink_user_throughput_bps_ue': float,
            'uplink_user_throughput_bps_ue': float,
            'e2e_latency_ms': float,
            'loss_probability': float,
            'jitter': float,
            'dscp_levels': List[int],
            '_5qi_levels': List[int],
            'active': bool
        }

        self.attribute_map = {
            'use_case_type': 'use_case_type',
            'service_slice_type_sst': 'service_slice_type_sst',
            'downlink_user_throughput_bps_ue': 'downlink_user_throughput_bps_ue',
            'uplink_user_throughput_bps_ue': 'uplink_user_throughput_bps_ue',
            'e2e_latency_ms': 'e2e_latency_ms',
            'loss_probability': 'loss_probability',
            'jitter': 'jitter',
            'dscp_levels': 'dscp_levels',
            '_5qi_levels': '_5qi_levels',
            'active': 'active'
        }
        self._use_case_type = use_case_type
        self._service_slice_type_sst = service_slice_type_sst
        self._downlink_user_throughput_bps_ue = downlink_user_throughput_bps_ue
        self._uplink_user_throughput_bps_ue = uplink_user_throughput_bps_ue
        self._e2e_latency_ms = e2e_latency_ms
        self._loss_probability = loss_probability
        self._jitter = jitter
        self._dscp_levels = dscp_levels
        self.__5qi_levels = _5qi_levels
        self._active = active

    @classmethod
    def from_dict(cls, dikt) -> 'SliceCreate':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The SliceCreate of this SliceCreate.  # noqa: E501
        :rtype: SliceCreate
        """
        return util.deserialize_model(dikt, cls)

    @property
    def use_case_type(self) -> str:
        """Gets the use_case_type of this SliceCreate.


        :return: The use_case_type of this SliceCreate.
        :rtype: str
        """
        return self._use_case_type

    @use_case_type.setter
    def use_case_type(self, use_case_type: str):
        """Sets the use_case_type of this SliceCreate.


        :param use_case_type: The use_case_type of this SliceCreate.
        :type use_case_type: str
        """

        self._use_case_type = use_case_type

    @property
    def service_slice_type_sst(self) -> str:
        """Gets the service_slice_type_sst of this SliceCreate.

        Network slice type  # noqa: E501

        :return: The service_slice_type_sst of this SliceCreate.
        :rtype: str
        """
        return self._service_slice_type_sst

    @service_slice_type_sst.setter
    def service_slice_type_sst(self, service_slice_type_sst: str):
        """Sets the service_slice_type_sst of this SliceCreate.

        Network slice type  # noqa: E501

        :param service_slice_type_sst: The service_slice_type_sst of this SliceCreate.
        :type service_slice_type_sst: str
        """
        allowed_values = ["eMBB", "uRLLC", "mMTC", "uLBC"]  # noqa: E501
        if service_slice_type_sst not in allowed_values:
            raise ValueError(
                "Invalid value for `service_slice_type_sst` ({0}), must be one of {1}"
                .format(service_slice_type_sst, allowed_values)
            )

        self._service_slice_type_sst = service_slice_type_sst

    @property
    def downlink_user_throughput_bps_ue(self) -> float:
        """Gets the downlink_user_throughput_bps_ue of this SliceCreate.

        Downlink bandwidth in bps  # noqa: E501

        :return: The downlink_user_throughput_bps_ue of this SliceCreate.
        :rtype: float
        """
        return self._downlink_user_throughput_bps_ue

    @downlink_user_throughput_bps_ue.setter
    def downlink_user_throughput_bps_ue(self, downlink_user_throughput_bps_ue: float):
        """Sets the downlink_user_throughput_bps_ue of this SliceCreate.

        Downlink bandwidth in bps  # noqa: E501

        :param downlink_user_throughput_bps_ue: The downlink_user_throughput_bps_ue of this SliceCreate.
        :type downlink_user_throughput_bps_ue: float
        """
        if downlink_user_throughput_bps_ue is None:
            raise ValueError("Invalid value for `downlink_user_throughput_bps_ue`, must not be `None`")  # noqa: E501

        self._downlink_user_throughput_bps_ue = downlink_user_throughput_bps_ue

    @property
    def uplink_user_throughput_bps_ue(self) -> float:
        """Gets the uplink_user_throughput_bps_ue of this SliceCreate.

        Uplink bandwidth in bps  # noqa: E501

        :return: The uplink_user_throughput_bps_ue of this SliceCreate.
        :rtype: float
        """
        return self._uplink_user_throughput_bps_ue

    @uplink_user_throughput_bps_ue.setter
    def uplink_user_throughput_bps_ue(self, uplink_user_throughput_bps_ue: float):
        """Sets the uplink_user_throughput_bps_ue of this SliceCreate.

        Uplink bandwidth in bps  # noqa: E501

        :param uplink_user_throughput_bps_ue: The uplink_user_throughput_bps_ue of this SliceCreate.
        :type uplink_user_throughput_bps_ue: float
        """
        if uplink_user_throughput_bps_ue is None:
            raise ValueError("Invalid value for `uplink_user_throughput_bps_ue`, must not be `None`")  # noqa: E501

        self._uplink_user_throughput_bps_ue = uplink_user_throughput_bps_ue

    @property
    def e2e_latency_ms(self) -> float:
        """Gets the e2e_latency_ms of this SliceCreate.

        Latency in milliseconds  # noqa: E501

        :return: The e2e_latency_ms of this SliceCreate.
        :rtype: float
        """
        return self._e2e_latency_ms

    @e2e_latency_ms.setter
    def e2e_latency_ms(self, e2e_latency_ms: float):
        """Sets the e2e_latency_ms of this SliceCreate.

        Latency in milliseconds  # noqa: E501

        :param e2e_latency_ms: The e2e_latency_ms of this SliceCreate.
        :type e2e_latency_ms: float
        """
        if e2e_latency_ms is None:
            raise ValueError("Invalid value for `e2e_latency_ms`, must not be `None`")  # noqa: E501

        self._e2e_latency_ms = e2e_latency_ms

    @property
    def loss_probability(self) -> float:
        """Gets the loss_probability of this SliceCreate.

        Tolerable loss probability  # noqa: E501

        :return: The loss_probability of this SliceCreate.
        :rtype: float
        """
        return self._loss_probability

    @loss_probability.setter
    def loss_probability(self, loss_probability: float):
        """Sets the loss_probability of this SliceCreate.

        Tolerable loss probability  # noqa: E501

        :param loss_probability: The loss_probability of this SliceCreate.
        :type loss_probability: float
        """
        if loss_probability is None:
            raise ValueError("Invalid value for `loss_probability`, must not be `None`")  # noqa: E501

        self._loss_probability = loss_probability

    @property
    def jitter(self) -> float:
        """Gets the jitter of this SliceCreate.

        Tolerable jitter in milliseconds  # noqa: E501

        :return: The jitter of this SliceCreate.
        :rtype: float
        """
        return self._jitter

    @jitter.setter
    def jitter(self, jitter: float):
        """Sets the jitter of this SliceCreate.

        Tolerable jitter in milliseconds  # noqa: E501

        :param jitter: The jitter of this SliceCreate.
        :type jitter: float
        """
        if jitter is None:
            raise ValueError("Invalid value for `jitter`, must not be `None`")  # noqa: E501

        self._jitter = jitter

    @property
    def dscp_levels(self) -> List[int]:
        """Gets the dscp_levels of this SliceCreate.

        An array of the decimal dscp values that the slice supports  # noqa: E501

        :return: The dscp_levels of this SliceCreate.
        :rtype: List[int]
        """
        return self._dscp_levels

    @dscp_levels.setter
    def dscp_levels(self, dscp_levels: List[int]):
        """Sets the dscp_levels of this SliceCreate.

        An array of the decimal dscp values that the slice supports  # noqa: E501

        :param dscp_levels: The dscp_levels of this SliceCreate.
        :type dscp_levels: List[int]
        """
        if dscp_levels is None:
            raise ValueError("Invalid value for `dscp_levels`, must not be `None`")  # noqa: E501

        self._dscp_levels = dscp_levels

    @property
    def _5qi_levels(self) -> List[int]:
        """Gets the _5qi_levels of this SliceCreate.

        An array of the decimal dscp values that the slice supports  # noqa: E501

        :return: The _5qi_levels of this SliceCreate.
        :rtype: List[int]
        """
        return self.__5qi_levels

    @_5qi_levels.setter
    def _5qi_levels(self, _5qi_levels: List[int]):
        """Sets the _5qi_levels of this SliceCreate.

        An array of the decimal dscp values that the slice supports  # noqa: E501

        :param _5qi_levels: The _5qi_levels of this SliceCreate.
        :type _5qi_levels: List[int]
        """
        if _5qi_levels is None:
            raise ValueError("Invalid value for `_5qi_levels`, must not be `None`")  # noqa: E501

        self.__5qi_levels = _5qi_levels

    @property
    def active(self) -> bool:
        """Gets the active of this SliceCreate.

        If the network slice is active in the network  # noqa: E501

        :return: The active of this SliceCreate.
        :rtype: bool
        """
        return self._active

    @active.setter
    def active(self, active: bool):
        """Sets the active of this SliceCreate.

        If the network slice is active in the network  # noqa: E501

        :param active: The active of this SliceCreate.
        :type active: bool
        """
        if active is None:
            raise ValueError("Invalid value for `active`, must not be `None`")  # noqa: E501

        self._active = active
