#include <iostream>
#include <message.h>
#include <csignal>
#include "Server.h"

using namespace std;

void sigHandler(int) {
  cout<<"Bye"<<endl;
  exit(0);
}

int main(int argc, char *argv[]) {
  signal(SIGINT, sigHandler);
  Server server(argc, argv);
  server.start();
}