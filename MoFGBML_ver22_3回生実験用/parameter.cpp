// 173

#include <iostream>
#include <string>
#include <vector>
#include <algorithm>
#include <numeric>
#include <queue>
#include <iomanip>
#include <climits>
#include <string>
#include <cmath>

using namespace std;
using ll = long long int;
#define NUM 1000000007

int gcd(int x, int y){
    if(x%y == 0)  return y;
    else return gcd(y, x%y);
}

string func(int x, int y){
    if (x <= 0) return "0f";
    else if(x/y >= 1) return "1f";
    else {
        int buf = gcd(x, y);
        return to_string(x/buf) + "f/" + to_string(y/buf) + "f";
    }
}

int main(){
    int tmp, buf;
    cout << "num of sections:" << endl;
    cin >> tmp;
    cout << "rectangle:0 trapezoid:1 triangle:2 gaussian:3" << endl;
    cin >> buf;
    switch (buf){
        case 0:
            cout << "rectangle" << endl;
            for(int x=2; x<=tmp; x++){
                cout << "//" << x << endl;
                for(int i=0; i<x; i++){
                    cout << "new float[] {" << func(i, x) << ", " << func(i+1, x) << "}," << endl;
                }
            }
            break;
        case 1:
            cout << "trapezoid" << endl;
            for(int x=2; x<=tmp; x++){
                cout << "//" << x << endl;
                for(int i=0; i<x; i++){
                    cout << "new float[] {" << func(i*4-3, (x-1)*4) << ", " << func(i*4-1, (x-1)*4) << ", " << func(i*4+1, (x-1)*4) << ", " << func(i*4+3, (x-1)*4) << "}," << endl;
                }
            }
            break;
        case 2:
            cout << "triangle" << endl;
            for(int x=2; x<=tmp; x++){
                cout << "//" << x << endl;
                for(int i=0; i<x; i++){
                    cout << "new float[] {" << func(i-1, x-1) << ", " << func(i, x-1) << ", " << func(i+1, x-1) << "}," << endl;
                }
            }
            break;
        case 3:
            cout << "gaussian" << endl;
            for(int x=2; x<=tmp; x++){
                cout << "//" << x << endl;
                cout << "calcGaussParam(" << func(0, x-1) << ", " << func(1, (x-1)*2) << ", 0.5f)," << endl;
                for(int i=1; i<x; i++){
                    cout << "calcGaussParam(" << func(i, x-1) << ", " << func(i*2-1, (x-1)*2) << ", 0.5f)," << endl;
                }    
            }
            break;                        
    }
    return 0;
}
