import hashlib
import itertools

#iterates over all 6 digit combinations
for combination in itertools.product(range(10), repeat=6):
    pin = ''.join(str(x) for x in combination).encode()
    for i in range(0, 25 * 400):
        m = hashlib.md5()
        m.update(pin)
        pin = m.digest()

    if pin.hex() == "d04988522ddfed3133cc24fb6924eae9":
        print(''.join(str(x) for x in combination))
        break;