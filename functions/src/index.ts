import * as functions from 'firebase-functions';
const { Storage } = require('@google-cloud/storage');

const mkdirp = require('mkdirp-promise');
const spawn = require('child-process-promise').spawn;
const fs = require('fs');
import * as sharp from 'sharp';
import * as fileSystem from 'fs-extra';
import { join } from 'path';

const admin = require('firebase-admin');
const path = require('path')
const os = require('os')

admin.initializeApp();

const gcs = new Storage();
const operatorProfileImageConvertedSizes = [250]



export const optimizeOperatorImage = functions.storage.object().onFinalize(async object => {
    const bucket = gcs.bucket(object.bucket)
    // dove si trova il file nello storage
    const filePath = object.name;
    // fa split rispetto a '/' e prende l'ultimo elemento dell'array
    const fileName = filePath.split('/').pop();

    // metadata file
    const metadata = {
        contentType: 'image/jpeg',
    }

    // break point per evitare che la funzione trigegri all'infinito
    // di base questa funzione va ogni volta che viene aggiunta un immagine
    // quindi riscrivendo nello storage l'immagine ridimensionata il loop sarebbe infito
    if (!object.contentType.includes('image')) {
        console.log('@resize -> not an image');
        return false;
    }

    if (!filePath.includes('OPERATORS_IMAGES')) {
        console.log('@resize -> not in the correct folder');
        return false;
    }

    if (fileName.includes('optimized@')) {
        console.log('@resize -> alread modified')
        return false
    }

    // array di promises
    const imageOptimizedPromise = operatorProfileImageConvertedSizes.map(async size => {
        // nome con tempo aggiunto
        const thumbPath = join(path.dirname(filePath), `optimized@${fileName}`);
        const thumbnailUploadStream = bucket.file(thumbPath).createWriteStream({ metadata });

        const pipeline = sharp();

        pipeline.resize(size, size).jpeg()
            .pipe(thumbnailUploadStream);

        bucket.file(filePath).createReadStream().pipe(pipeline);

        return new Promise((resolve, reject) => thumbnailUploadStream.on('finish', resolve).on('error', reject));
    });

    // chiamiamo tutte le promises nell'array
    await Promise.all(imageOptimizedPromise).then(() => {
        bucket.file(filePath).delete()
    })
    return true
})

export const cloneOperatorInOperatorNode =
    functions.firestore.document('Countries/{countryId}/Zones/{zoneId}/Operators/{operatorId}')
        .onWrite(async (snap, context) => {
            const operator = snap.after.exists ? snap.after.data() : null;
            if (operator != null) {
                const operatorId = context.params.operatorId
                admin.firestore().doc(`Operators/${operatorId}`).set(operator)
            } else {
                return null
            }
        })

export const increaseCounterOnCreate =
    functions.firestore.document('Countries/{countryId}/Zones/{zoneId}/Operators/{operatorId}')
        .onCreate(async (newOperator, context) => {
            const group = newOperator.data().group
            const category = newOperator.data().category
            const zoneId = context.params.zoneId
            const countryId = context.params.countryId

            // home service
            if (group == 0) {
                switch (category) {
                    case 0:
                        admin.firestore().runTransaction(transaction => {
                            let documentRef = admin.firestore().doc(`Counters/${countryId}/${zoneId}/physio`);
                            return transaction.get(documentRef).then(doc => {
                                if (doc.exists) {
                                    transaction.update(documentRef, { count: doc.get('count') + 1 });
                                } else {
                                    transaction.create(documentRef, { count: 1 });
                                }
                            });
                        })
                        break;
                    case 1:
                        admin.firestore().runTransaction(transaction => {
                            let documentRef = admin.firestore().doc(`Counters/${countryId}/${zoneId}/oss`);
                            return transaction.get(documentRef).then(doc => {
                                if (doc.exists) {
                                    transaction.update(documentRef, { count: doc.get('count') + 1 });
                                } else {
                                    transaction.create(documentRef, { count: 1 });
                                }
                            });
                        })
                        break;
                    case 2:
                        admin.firestore().runTransaction(transaction => {
                            let documentRef = admin.firestore().doc(`Counters/${countryId}/${zoneId}/nurse`);
                            return transaction.get(documentRef).then(doc => {
                                if (doc.exists) {
                                    transaction.update(documentRef, { count: doc.get('count') + 1 });
                                } else {
                                    transaction.create(documentRef, { count: 1 });
                                }
                            });
                        })
                        break;
                    case 3:
                        admin.firestore().runTransaction(transaction => {
                            let documentRef = admin.firestore().doc(`Counters/${countryId}/${zoneId}/eldercare`);
                            return transaction.get(documentRef).then(doc => {
                                if (doc.exists) {
                                    transaction.update(documentRef, { count: doc.get('count') + 1 });
                                } else {
                                    transaction.create(documentRef, { count: 1 });
                                }
                            });
                        })
                        break;

                    default:
                        break;
                }
            }

            if (group == 1) {
                admin.firestore().runTransaction(transaction => {
                    let documentRef = admin.firestore().doc(`Counters/${countryId}/${zoneId}/doctors`);
                    return transaction.get(documentRef).then(doc => {
                        if (doc.exists) {
                            transaction.update(documentRef, { count: doc.get('count') + 1 });
                        } else {
                            transaction.create(documentRef, { count: 1 });
                        }
                    });
                })
            }

        })


export const decreaseCounterOnCreate =
    functions.firestore.document('Countries/{countryId}/Zones/{zoneId}/Operators/{operatorId}')
        .onDelete(async (newOperator, context) => {
            const group = newOperator.data().group
            const category = newOperator.data().category
            const zoneId = context.params.zoneId
            const countryId = context.params.countryId

            // home service
            if (group == 0) {
                switch (category) {
                    case 0:
                        admin.firestore().runTransaction(transaction => {
                            let documentRef = admin.firestore().doc(`Counters/${countryId}/${zoneId}/physio`);
                            return transaction.get(documentRef).then(doc => {
                                if (doc.exists) {
                                    transaction.update(documentRef, { count: doc.get('count') - 1 });
                                }
                            });
                        })
                        break;
                    case 1:
                        admin.firestore().runTransaction(transaction => {
                            let documentRef = admin.firestore().doc(`Counters/${countryId}/${zoneId}/oss`);
                            return transaction.get(documentRef).then(doc => {
                                if (doc.exists) {
                                    transaction.update(documentRef, { count: doc.get('count') - 1 });
                                }
                            });
                        })
                        break;
                    case 2:
                        admin.firestore().runTransaction(transaction => {
                            let documentRef = admin.firestore().doc(`Counters/${countryId}/${zoneId}/nurse`);
                            return transaction.get(documentRef).then(doc => {
                                if (doc.exists) {
                                    transaction.update(documentRef, { count: doc.get('count') - 1 });
                                }
                            });
                        })
                        break;
                    case 3:
                        admin.firestore().runTransaction(transaction => {
                            let documentRef = admin.firestore().doc(`Counters/${countryId}/${zoneId}/eldercare`);
                            return transaction.get(documentRef).then(doc => {
                                if (doc.exists) {
                                    transaction.update(documentRef, { count: doc.get('count') - 1 });
                                }
                            });
                        })
                        break;

                    default:
                        break;
                }
            }

            if (group == 1) {
                admin.firestore().runTransaction(transaction => {
                    let documentRef = admin.firestore().doc(`Counters/${countryId}/${zoneId}/doctors`);
                    return transaction.get(documentRef).then(doc => {
                        if (doc.exists) {
                            transaction.update(documentRef, { count: doc.get('count') + 1 });
                        } else {
                            transaction.create(documentRef, { count: 1 });
                        }
                    });
                })
            }

        })

