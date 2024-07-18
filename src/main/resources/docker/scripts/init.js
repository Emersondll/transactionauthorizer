db.createUser(
	{
		user: 'user',
		pwd: 'password',
		roles: [{role: 'readWrite', db: 'miniautorizador'}]
	}
);

db = db.getSiblingDB('miniautorizador');

// Criando a coleção de MCC (Merchant Category Codes)
db.createCollection('mcc');
// Inserindo alguns dados iniciais na coleção de MCC
db.mcc.insertMany([
  { code: '5411', description: 'FOOD' },
  { code: '5412', description: 'FOOD' },
  { code: '5811', description: 'MEAL' },
  { code: '5812', description: 'MEAL' }
]);

// Verifica se a coleção de Balance já existe, caso contrário, cria e insere dados

db.createCollection('balance');
  db.balance.insertMany([
   { account: '1234567890', food: 100.00, meal: 50.00, cash: 200.00 },
   { account: '1234567891', food: 150.00, meal: 60.00, cash: 300.00 },
   { account: '1234567892', food: 200.00, meal: 70.00, cash: 400.00 }
  ]);


// Criando a coleção de Merchant (comerciantes)
db.createCollection('merchant');

// Inserindo alguns dados iniciais na coleção de Merchant
db.merchant.insertMany([
  { name: 'UBER TRIP                   SAO PAULO BR', mcc: '5411' },
  { name: 'UBER EATS                   SAO PAULO BR', mcc: '5812' },
  { name: 'PAG*JoseDaSilva          RIO DE JANEI BR', mcc: '5814' },
  { name: 'PICPAY*BILHETEUNICO           GOIANIA BR', mcc: '5814' },
]);
