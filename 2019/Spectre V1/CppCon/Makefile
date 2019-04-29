
CXX= c++
CXXFLAGS= -Wall --std=c++17 -O3

all: cache spectrev1

cache: cache.o
	$(CXX) $(CXXFLAGS) -o $@ cache.o

spectrev1: spectrev1.o
	$(CXX) $(CXXFLAGS) -o $@ spectrev1.o

clean:
	rm -f cache spectrev1 *.o
