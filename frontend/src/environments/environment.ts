export const environment = {
  production: true,
  // En production, le frontend et l'API sont servis derrière le même ingress (voir k8s/base/ingress.yaml)
  apiBaseUrl: '/api/v1'
};
