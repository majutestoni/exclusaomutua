# Exclusão mutua

1.	Descreva de maneira geral o algoritmo de exclusão mútua centralizado e comente suas vantagens e desvantagens.
O algoritmo controla o acesso a uma região crítica em sistemas distribuídos. Um processo específico (Coordenador central) atua como coordenador e gerencia o acesso ao recurso compartilhado. Quando um processo deseja acessar o recurso, ele envia uma requisição ao coordenador através de uma solicitação de acesso. Caso o recurso esteja livre, o solicitante tem a sua permissão concedida pelo coordenador. A requisição é armazenada em uma fila de espera quando outro processo já estiver utilizando o recurso.
 
A liberação do recurso é feita quando o processo que está usando o recurso termina, enviando uma mensagem de liberação para o coordenador, e a próxima requisição é feita quando o coordenador concede acesso ao próximo processo na fila.
 
Vantagens:
-	Como temos apenas um único coordenador, conseguimos reduzir os riscos de deadlocks já que apenas um coordenador está gerenciando as requisições. 
-	 Como há apenas um único ponto de controle, a sua implementação é simplificada. 
-	 O acesso é justo já que o coordenador consegue manter uma fila de espera ordenada.

Desvantagens:
-	Se o coordenador falhar, o sistema inteiro pode parar.
-	O coordenador pode limitar a eficiência do sistema em sistemas grandes, causando gargalos.


2.	Os processos P1, P2 e P3 solicitam entrada em uma seção crítica, neste momento seus relógios lógicos são 5, 1 e 4 respectivamente. Apresente as mensagens trocadas pelo algoritmo de exclusão mútua distribuída até que todos os processos tenham passado pela seção crítica.
Passo 1:
-	P2 tem o menor timestamp, logo ele recebe OK dos outros processos e entra na seção crítica: P1 -> P2 (OK) e P3 -> P2 (OK)
-	P2 envia mensagens de liberação: P2 -> P1(Liberação) e P2 -> P3 (Liberação)
Passo 2:
-	P3 tem o segundo menor timestamp, logo ele recebe OK dos outros processos e entra na seção crítica: P1 -> P3 (OK)
-	P3 envia mensagens de liberação: P3 -> P1 (Liberação)
Passo 3:
-	P1 é o único que resta na fila. Como P1 tem o maior timestamp, ele recebe OK e entra na seção crítica. Nenhuma mensagem extra é necessária, pois todos já passaram pela seção crítica. Sendo assim, a ordem final de acesso à seção crítica fica: P2 -> P3-> P1
![image](https://github.com/user-attachments/assets/f0ce6a8f-7fbd-486d-a56d-03974a3a2847)
 

3.	Em um ambiente com cinco processos distribuídos, enumerados de 0 a 4, o processo 3 percebe que o coordenador (processo 4) não está mais ativo, e inicia uma eleição. Descreva como ocorre esta eleição no algoritmo de Ring e no algoritmo de Bully

Resposta:  Algoritmo de Bully:
![image](https://github.com/user-attachments/assets/42c63160-2a84-4c8b-af49-a5d4d2d684d9)

O processo 3 irá identificar que o processo 4 (Coordenador) morreu e irá disparar um comunicado para todos os outros processos para validar se há alguém maior que ele para assumir a posição. Como não há ele se torna o coordenador.

 

Comunicação:
![image](https://github.com/user-attachments/assets/fb67388a-438f-469d-a55e-3dc51aff3cfb)

 
Novo Coordenador:
 ![image](https://github.com/user-attachments/assets/9a119fd8-ca21-4c5e-9735-ffb6dc34a153)


Algoritmo de Ring:

O algoritmo inicia como uma lista vazia Lista [], a cada processo é incluído a respectivo processo na lista.

Ou seja:
![image](https://github.com/user-attachments/assets/d11195ad-c669-488c-b01a-6290f906ffef)

 
P3 manda mensagem e percebe que P4 está morto. Como P4 era sucessor de P3, para fechar o ciclo, P0 passa a ser o sucessor de P3.
P3 envia mensagem para P0 | Lista [P3]
P0 envia mensagem para P1 | Lista [P3, P0]
P1 envia mensagem para P2 | Lista [P3, P0, P1]
P2 envia mensagem para P3 | Lista [P3, P0, P1, P2]
Neste momento P3 recebe mensagem de P2 e percebe que ele é o maior processo (ID) ativo e retorna ao P2 que ele agora é o Coordenador e o P2 retorna ao P1 informando que P3 agora é o novo coordenador e assim sucessivamente até a mensagem voltar ao P3.
 ![image](https://github.com/user-attachments/assets/84f9d400-0d0c-4b6e-8956-acff1eeb7672)


4.	Compare os algoritmos de Bully e Ring e escolha o de sua preferência justificando sua escolha

Resposta: 
Bully Vantagens:
Eficiência em falhas – Garante que o nó com maior ID se torne líder.
Simplicidade – Fácil de entender e implementar.

Bully Desvantagens:
Alto custo de comunicação – Envia muitas mensagens, especialmente em grandes redes.
Lentidão em falhas simultâneas – Pode demorar para estabilizar se vários nós falharem ao mesmo tempo.
Sobrecarga no líder – O nó eleito pode ficar sobrecarregado dependendo da aplicação.

Ring Vantagens:

Menos tráfego de mensagens – Usa comunicação unidirecional.
Descentralização – Todos os nós participam igualmente da eleição, sem precisar conhecer todos os outros nós.
Mais eficiente em grandes redes – Seu tráfego cresce linearmente com o número de nós.

Ring Desvantagens:

Maior tempo de eleição – Pode demorar mais, pois as mensagens percorrem todo o ciclo.
Sensível a falhas – Se um nó no caminho falhar, a eleição pode ser interrompida ou exigir retransmissão.
Mais complexo de implementar – Precisa garantir que as mensagens circulem corretamente no ciclo.

O melhor algoritmo é o de Ring, pois em projetos/sistemas mais robustos ele se torna mais custoso devido ao tempo de mensagens trocadas, mas em contrapartida se tem um maior controle do sistema.
Já para sistemas de menor porte o algoritmo de Bully é mais recomendado devido à maior facilidade de utilização e o ID com maior valor sempre será o Coordenador.
