# coding: utf-8

import sys
from setuptools import setup, find_packages

NAME = "swagger_server"
VERSION = "1.0.0"
# To install the library, run the following
#
# python setup.py install
#
# prerequisite: setuptools
# http://pypi.python.org/pypi/setuptools

REQUIRES = [
    "connexion",
    "swagger-ui-bundle>=0.0.2"
]

setup(
    name=NAME,
    version=VERSION,
    description="Boosting 5G with Network Coding-Aware Slicing",
    author_email="rafaelg@student.dei.uc.pt",
    url="",
    keywords=["Swagger", "Boosting 5G with Network Coding-Aware Slicing"],
    install_requires=REQUIRES,
    packages=find_packages(),
    package_data={'': ['swagger/swagger.yaml']},
    include_package_data=True,
    entry_points={
        'console_scripts': ['swagger_server=swagger_server.__main__:main']},
    long_description="""\
    This is an API for informing an SDN controller of potential network slices in its infrastructure, requesting infrastructure information to improve Network Coding algorithms and introduce link metrics of the infrastructure. Used in the work [♠](https://www.overleaf.com/project/65c4bf4599ae6fb64d611bf0) 
    """
)
