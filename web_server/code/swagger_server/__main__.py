#!/usr/bin/env python3

import connexion

from swagger_server import encoder


def main():
    app = connexion.App(__name__, specification_dir='./swagger/')
    app.json = encoder.JSONEncoder
    app.add_api('swagger.yaml', arguments={'title': 'Boosting 5G with Network Coding-Aware Slicing'}, pythonic_params=True)
    app.run(port=8080, debug=True)


if __name__ == '__main__':
    main()
