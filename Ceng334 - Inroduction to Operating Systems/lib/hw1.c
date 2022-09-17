#include "message.h"
#include "logging.h"
#include <stdio.h>
#include <stdlib.h>
#include<unistd.h>
#include<string.h>
#include<poll.h>
#include<sys/wait.h>
#include<sys/time.h>
#include<sys/socket.h>
#include <errno.h>

#define PIPE(fd) socketpair(AF_UNIX, SOCK_STREAM, PF_UNIX, fd)

typedef struct Bidders{
	char* b;
	int number_of_arguments;
	int pid;
	char** arguments;
	struct bidders* next; 	
}Bidders;
void server(int* waited,struct Bidders* head,int** pipeServer,int starting_bid, int minimum_increment, int number_of_bidders );
int main()
{
	int i=0,j=0,starting_bid, minimum_increment, number_of_bidders,sss;
	char* bidders;
	char* oc;
	int num_arguments,pidg;
	int* args;
	int** pipe1;
	char** char_args;
	Bidders* head=NULL;
	Bidders* curr=NULL;
	Bidders* new_bidder=NULL;
	//curr =(struct Bidders*)malloc(sizeof(struct Bidders));

	scanf("%d %d %d",&starting_bid,&minimum_increment,&number_of_bidders);
	//printf("%d %d %d\n",starting_bid,minimum_increment,number_of_bidders );
	if (number_of_bidders>0){
		head =(struct Bidders*)malloc(sizeof(struct Bidders));		
		bidders=malloc(40);
		scanf("%s %d",bidders,&num_arguments);
		char_args=(char**)malloc((num_arguments+2)*sizeof(char*));
		char_args[0]=bidders;
		for (i = 1; i < num_arguments+1; i++){
			oc=(char*)malloc(100*sizeof(char));
			scanf("%s",oc);
			//printf("%s\n",oc );
			char_args[i]=oc;
			//pipe(pipe1[i]);
			oc=NULL;
			free(oc);
			
		}
		char_args[num_arguments+1]=(char*)0;
		head->b=bidders;
		bidders=NULL;
		head->number_of_arguments=num_arguments;
		head->arguments=char_args;
		char_args=NULL;
		head->next=NULL;
		free(char_args);
		free(bidders);
	}
	//printf("\n" );
	/*printf("head: %s %d ",head->b,head->number_of_arguments);
		for (i = 0; i < num_arguments; i++){
			printf("%s ",head->arguments[i]);
		}
	printf("\n" );
	printf("%d\n",number_of_bidders );
	printf("\n" );*/

	curr=head;
	for (i = 0; i < (number_of_bidders-1); i++){
		new_bidder = (struct Bidders*)malloc(sizeof(struct Bidders));

		bidders=malloc(40);
		scanf("%s %d",bidders,&num_arguments);
		char_args=(char**)malloc((num_arguments+2)*sizeof(char*));
		char_args[0]=bidders;
		for (j = 1; j < num_arguments+1; j++){
			oc=(char*)malloc(100*sizeof(char));
			scanf("%s",oc);
			//printf("%s\n",oc );
			char_args[j]=oc;
			//pipe(pipe1[i]);
			oc=NULL;
			free(oc);
		}
		char_args[num_arguments+1]=(char*)0;
		new_bidder->b=bidders;
		new_bidder->number_of_arguments=num_arguments;
		new_bidder->arguments=char_args;
		bidders=NULL;
		new_bidder->next=NULL;
		char_args=NULL;
		free(char_args);
		free(bidders);
		curr->next=new_bidder;
		curr=curr->next;
		new_bidder=NULL;
		free(new_bidder);
	}

	/*printf("head2: %s %d ",head->b,head->number_of_arguments);
		for (i = 0; i < head->number_of_arguments; i++){
			printf("%s ",head->arguments[i]);
		}
	printf("\n" );*/

	
	curr=head;
	pipe1=(int**)malloc(number_of_bidders*sizeof(int));
	for(i=0;i<number_of_bidders;i++){
		args=(int*)malloc(2*sizeof(int));
		pipe1[i]=args;
		PIPE(pipe1[i]);
		args=NULL;
		free(args);
		pidg=fork();
		if(pidg){
			//printf("asdsadsad:::: %d\n",pidg );
			curr->pid=pidg;
			curr=curr->next;
		}
		else{
			//printf("%s %s\n",curr->b,curr->arguments[0] );
			close(pipe1[i][0]);
			dup2(pipe1[i][1],0);
			dup2(pipe1[i][1],1);
			close(pipe1[i][1]);
			execv(curr->b,curr->arguments);
			//printf("err: %s\n",strerror(errno) );
		}
	}
	/*curr=head;
	while(curr){
		printf("pidbs-->>> %s %d ",curr->b,curr->pid);
		
		printf("\n");
		curr=curr->next;
	}*/

	server(&sss,head,pipe1,starting_bid,minimum_increment,number_of_bidders);

	return 0;
}
int bo(struct pollfd* pfd,int number_of_bidders){
	int i;
	for(i=0;i<number_of_bidders;i++){
		if(pfd[i].fd>=0){
			//printf("%d->>>%d\n",i,pfd[i].fd);
			return 1;	
		} 
		else continue;
	}
	return 0;
}

void server(int* waited,struct Bidders* head,int** pipeServer,int starting_bid_input, int minimum_increment_input, int number_of_bidders ){
	struct  pollfd* pfd;
	Bidders* curr=NULL;
	int i,j,r,current_bid_func,u_client_id=0,winner_id_func,num_of_online=number_of_bidders;
	cm new_message[1];
	sm server_message[1];
	ii input_data[1];
	oi output_data[1];
	int* statuses;
	statuses=(int*)malloc(sizeof(int)*number_of_bidders);
	pfd=(struct pollfd*)malloc(number_of_bidders*sizeof(struct  pollfd));
	for (i = 0; i < number_of_bidders; i++){
		pfd[i].fd = pipeServer[i][0];
		pfd[i].events=POLLIN;
		pfd[i].revents=0;
		close(pipeServer[i][1]);
	}
	//printf("pid:%d\n",head->pid);
	/*curr=head;
	while(curr){
		printf("pid-->>%s %d ",curr->b,curr->pid);
		for (i = 0; i < curr->number_of_arguments; i++){
			printf("%s ",curr->arguments[i]);
		}
		printf("\n");
		curr=curr->next;
	}*/
	
	while(bo(pfd,number_of_bidders)){
		//printf("%d\n",bo(pfd,number_of_bidders));
		poll(pfd,number_of_bidders,0);
		for(i=0;i<number_of_bidders;i++){
			if(pfd[i].revents && POLLIN){
				r=read(pfd[i].fd,new_message,sizeof(cm));
				input_data->type=new_message->message_id;
				curr=head;
				/*printf("pits:: ");
				for(j=0;j<i;j++){
					printf("%d %d - ", j, curr->pid);
					curr=curr->next;
					printf("after-->%d %d - ", j, curr->pid);
				}
				printf("\n");*/

				input_data->pid=curr->pid;
				input_data->info=new_message->params;
				print_input(input_data,i);
				if (r == 0) 			/* EOF */
					pfd[i].fd = -1;   /* poll() ignores pollfd item if fd is negative */
				else{
					//printf("meesage id:%d\n",new_message->message_id);
					pfd[i].revents=0;
					if(new_message->message_id==CLIENT_CONNECT){
						server_message->message_id=SERVER_CONNECTION_ESTABLISHED;
						server_message->params.start_info.starting_bid= starting_bid_input;
						server_message->params.start_info.current_bid=current_bid_func;
						server_message->params.start_info.minimum_increment= minimum_increment_input;
						server_message->params.start_info.client_id=i;
						write(pfd[i].fd,server_message,sizeof(sm));
						output_data->type=server_message->message_id;
						curr=head;
						for(j=0;j<i;j++){
							curr=curr->next;
						}

						output_data->pid=curr->pid;
						output_data->info=server_message->params;
						print_output(output_data, i);
					}
					else if(new_message->message_id==CLIENT_BID){
						if(new_message->params.bid<starting_bid_input){//BID lower than starting bid
							server_message->message_id=SERVER_BID_RESULT;
							server_message->params.result_info.result=BID_LOWER_THAN_STARTING_BID;
							server_message->params.result_info.current_bid=current_bid_func;
							write(pfd[i].fd,server_message,sizeof(sm));
							output_data->type=server_message->message_id;
							curr=head;
							for(j=0;j<i;j++){
								curr=curr->next;
							}

							output_data->pid=curr->pid;
							output_data->info=server_message->params;
							print_output(output_data, i);
						}
						else if(new_message->params.bid<current_bid_func){//BID lower than current
							server_message->message_id=SERVER_BID_RESULT;
							server_message->params.result_info.result=BID_LOWER_THAN_CURRENT;
							server_message->params.result_info.current_bid=current_bid_func;
							write(pfd[i].fd,server_message,sizeof(sm));
							output_data->type=server_message->message_id;
							curr=head;
							for(j=0;j<i;j++){
								curr=curr->next;
							}

							output_data->pid=curr->pid;
							output_data->info=server_message->params;
							print_output(output_data, i);
						}
						else if(new_message->params.bid-current_bid_func<minimum_increment_input){//BID lower than increment
							server_message->message_id=SERVER_BID_RESULT;
							server_message->params.result_info.result=BID_INCREMENT_LOWER_THAN_MINIMUM;
							server_message->params.result_info.current_bid=current_bid_func;
							write(pfd[i].fd,server_message,sizeof(sm));
							output_data->type=server_message->message_id;
							curr=head;
							for(j=0;j<i;j++){
								curr=curr->next;
							}

							output_data->pid=curr->pid;
							output_data->info=server_message->params;
							print_output(output_data, i);
						}
						else{//BID accepted
							server_message->message_id=SERVER_BID_RESULT;
							server_message->params.result_info.result=BID_ACCEPTED;
							current_bid_func=new_message->params.bid;
							winner_id_func=i;
							server_message->params.result_info.current_bid=current_bid_func;
							write(pfd[i].fd,server_message,sizeof(sm));
							output_data->type=server_message->message_id;
							curr=head;
							for(j=0;j<i;j++){
								curr=curr->next;
							}

							output_data->pid=curr->pid;
							output_data->info=server_message->params;
							print_output(output_data, i);
						}
					}
					else if(new_message->message_id==CLIENT_FINISHED){
						num_of_online-=1;
						statuses[i]=new_message->params.status;
						//printf("online:%d\n",num_of_online );
					}
					if(num_of_online==0){
						server_message->message_id=SERVER_AUCTION_FINISHED;
						server_message->params.winner_info.winner_id=winner_id_func;
						server_message->params.winner_info.winning_bid=current_bid_func;
						print_server_finished(winner_id_func,current_bid_func);
						for(j=0;j<number_of_bidders;j++){
							write(pfd[j].fd,server_message,sizeof(sm));
							close(pfd[j].fd);
							pfd[j].fd=-1;
							//printf("%d->>>%d\n",j,pfd[j].fd);
						}
						curr=head;
						for(j=0;j<number_of_bidders;j++){
							waitpid(curr->pid,waited,0);
							print_client_finished(j,statuses[j], statuses[j]==*waited);
							curr=curr->next;
						}
					}

				}
			}
		}
	}
}